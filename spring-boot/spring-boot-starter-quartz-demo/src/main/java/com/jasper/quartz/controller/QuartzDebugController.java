package com.jasper.quartz.controller;

import com.jasper.quartz.config.QuartzDemoProperties;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Quartz 调试接口：专门用于查看 {@code counterJob} 的累积值。
 *
 * <p>两个视角：
 * <ol>
 *   <li>{@code GET /api/quartz/debug/counter}：通过 {@code Scheduler#getJobDetail} 读取，
 *       RAM / JDBC 都可用，这是业务代码应该使用的方式；</li>
 *   <li>{@code GET /api/quartz/debug/counter/raw}：直接查 {@code QRTZ_JOB_DETAILS.JOB_DATA}
 *       并反序列化，只有 JDBC JobStore 才有意义，用于观察 Quartz 到底把什么写进了数据库。</li>
 * </ol>
 *
 * <p>为什么会有两种存储格式？
 * <ul>
 *   <li>{@code useProperties=false}（本 Demo 的 JDBC 配置）：Quartz 用 Java 序列化
 *       写入 {@code JobDataMap}，BLOB 以 {@code AC ED} 开头；</li>
 *   <li>{@code useProperties=true}：Quartz 写成 {@code java.util.Properties} 文本，
 *       值只能是 String，读取时要用 {@code Properties#load}。</li>
 * </ul>
 *
 * <p><b>安全警告</b>：反序列化不可信数据有 RCE 风险。本接口仅用于本地学习/排障，
 * 生产环境不要暴露，且不要把用户输入直接反序列化。
 */
@RestController
@RequestMapping("/api/quartz/debug")
public class QuartzDebugController {

    private static final String COUNTER_JOB_NAME = "counterJob";
    private static final String TABLE_PREFIX = "QRTZ_";

    private final Scheduler scheduler;
    private final QuartzDemoProperties properties;
    private final ObjectProvider<DataSource> dataSourceProvider;

    public QuartzDebugController(Scheduler scheduler,
                                 QuartzDemoProperties properties,
                                 ObjectProvider<DataSource> dataSourceProvider) {
        this.scheduler = scheduler;
        this.properties = properties;
        this.dataSourceProvider = dataSourceProvider;
    }

    /**
     * 通过 Scheduler API 读取累积值（推荐方式）。
     *
     * <pre>{@code
     * curl localhost:8090/api/quartz/debug/counter
     * }</pre>
     */
    @GetMapping("/counter")
    public Map<String, Object> counter() throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(COUNTER_JOB_NAME, properties.group());
        JobDetail detail = scheduler.getJobDetail(jobKey);
        if (detail == null) {
            throw new NoSuchElementException("Job 不存在: " + jobKey
                    + "，请确认 quartz-demo.bootstrap=true 或手动创建 counterJob");
        }

        JobDataMap data = detail.getJobDataMap();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("source", "Scheduler#getJobDetail");
        result.put("jobStore", scheduler.getMetaData().getJobStoreClass().getName());
        result.put("job", jobKey.toString());
        result.put("count", data.getIntValue("count"));
        result.put("lastFireTime", data.containsKey("lastFireTime")
                ? data.getLongValue("lastFireTime")
                : null);
        result.put("jobData", toJsonSafeMap(data));
        return result;
    }

    /**
     * 直接读取并反序列化 {@code QRTZ_JOB_DETAILS.JOB_DATA}。
     *
     * <p>只适用于 JDBC JobStore；memory 模式没有 DataSource，会返回 400。
     *
     * <pre>{@code
     * # 先以 JDBC 模式启动
     * ./gradlew :quartz-demo:bootRun --args='--spring.profiles.active=jdbc'
     * curl localhost:8090/api/quartz/debug/counter/raw
     * }</pre>
     */
    @GetMapping("/counter/raw")
    public Map<String, Object> rawCounter() throws SchedulerException {
        DataSource dataSource = dataSourceProvider.getIfAvailable();
        if (dataSource == null) {
            throw new IllegalArgumentException(
                    "当前没有 DataSource：raw 接口只适用于 JDBC JobStore，请用 --spring.profiles.active=jdbc 启动");
        }

        String sql = "SELECT JOB_DATA FROM " + TABLE_PREFIX + "JOB_DETAILS "
                + "WHERE SCHED_NAME = ? AND JOB_NAME = ? AND JOB_GROUP = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, scheduler.getSchedulerName());
            statement.setString(2, COUNTER_JOB_NAME);
            statement.setString(3, properties.group());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new NoSuchElementException("QRTZ_JOB_DETAILS 中找不到 counterJob");
                }

                byte[] bytes = resultSet.getBytes(1);
                if (bytes == null || bytes.length == 0) {
                    throw new IllegalStateException("counterJob 的 JOB_DATA 为空");
                }

                DeserializedData decoded = decodeJobData(bytes);
                Map<String, Object> result = new LinkedHashMap<>();
                result.put("source", TABLE_PREFIX + "JOB_DETAILS.JOB_DATA");
                result.put("jobStore", scheduler.getMetaData().getJobStoreClass().getName());
                result.put("bytes", bytes.length);
                result.put("serializedType", decoded.type());
                result.put("count", decoded.data().get("count"));
                result.put("jobData", decoded.data());
                return result;
            }
        } catch (SQLException | IOException | ClassNotFoundException ex) {
            throw new IllegalStateException("读取/反序列化 JOB_DATA 失败: " + ex.getMessage(), ex);
        }
    }

    /**
     * 根据 BLOB 开头判断格式并反序列化。
     *
     * <p>Java 序列化流魔数：0xAC 0xED；否则按 Properties 文本解析。
     */
    private DeserializedData decodeJobData(byte[] bytes) throws IOException, ClassNotFoundException {
        boolean javaSerialized = bytes.length >= 2
                && (bytes[0] & 0xFF) == 0xAC
                && (bytes[1] & 0xFF) == 0xED;

        if (javaSerialized) {
            try (ObjectInputStream input = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
                Object object = input.readObject();
                if (object instanceof Map<?, ?> map) {
                    return new DeserializedData(object.getClass().getName(), toJsonSafeMap(map));
                }
                throw new IllegalStateException("JOB_DATA 反序列化后不是 Map，而是: " + object.getClass().getName());
            }
        }

        // useProperties=true：Quartz 存的是 Properties 文本，所有值都是 String
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(bytes));
        Map<String, Object> data = new LinkedHashMap<>();
        properties.forEach((key, value) -> data.put(String.valueOf(key), value));
        return new DeserializedData(Properties.class.getName(), data);
    }

    /**
     * JobDataMap 里可能有任意对象；返回 JSON 前把非标量值转成字符串，避免序列化异常。
     */
    private Map<String, Object> toJsonSafeMap(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        source.forEach((key, value) -> {
            Object safeValue = (value == null
                    || value instanceof String
                    || value instanceof Number
                    || value instanceof Boolean)
                    ? value
                    : String.valueOf(value);
            result.put(String.valueOf(key), safeValue);
        });
        return result;
    }

    private record DeserializedData(String type, Map<String, Object> data) {
    }
}
