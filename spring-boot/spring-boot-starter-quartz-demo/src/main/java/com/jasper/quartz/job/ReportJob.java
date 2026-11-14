
package com.jasper.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
public class ReportJob implements Job {

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        long startTime = System.currentTimeMillis();

        JobDataMap data = context.getMergedJobDataMap();

        // 从 JobDataMap 获取参数
        String reportName = data.getString("reportName");
        String reportType = data.getString("reportType");

        if (reportName == null || reportName.isBlank()) {
            reportName = "每日业务报表";
        }

        if (reportType == null || reportType.isBlank()) {
            reportType = "DAILY";
        }

        log.info("开始生成报表，jobKey={}, reportName={}, reportType={}",
                context.getJobDetail().getKey(),
                reportName,
                reportType);

        try {
            // 模拟报表生成
            generateReport(reportName, reportType);

            long duration = System.currentTimeMillis() - startTime;

            log.info("报表生成成功，reportName={}, duration={}ms",
                    reportName, duration);

        } catch (Exception ex) {
            log.error("报表生成失败，reportName={}", reportName, ex);

            // 告诉 Quartz 本次 Job 执行失败
            throw new JobExecutionException(
                    "报表生成失败: " + reportName, ex, false);
        }
    }

    private void generateReport(String reportName, String reportType)
            throws InterruptedException {

        log.info("正在生成报表：{}, 类型：{}, 日期：{}",
                reportName, reportType, LocalDate.now());

        // 模拟业务处理
        Thread.sleep(2000);

        log.info("报表数据处理完成，时间：{}", LocalDateTime.now());
    }
}
