package jasper;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;

import java.util.Collections;

public class Generator {

    /** 数据源配置 */
    private static final DataSourceConfig.Builder DATA_SOURCE_CONFIG =
            new DataSourceConfig.Builder("jdbc:mysql://127.0.0.1:3307/learn", "root", "passwd")
                    .keyWordsHandler(new MySqlKeyWordsHandler());

    public static final String USER_HOME = System.getProperty("user.home");
    // 1. 定义项目的根路径（不包含 src/main/java）
    public static final String PROJECT_PATH = USER_HOME + "/code/java/person/springDemo";

    public static final String PARENT_PACKAGE = "com.jasper.springDemo";
    public static final String TABLE_NAME = "test_user";

    public static void main(String[] args) {
        FastAutoGenerator.create(DATA_SOURCE_CONFIG)
                .globalConfig(
                        builder -> {
                            builder.author("jasper")
                                    .commentDate("yyyy-MM-dd")
                                    // 2. 这里指定 Java 代码的输出根目录
                                    .outputDir(PROJECT_PATH + "/src/main/java")
                                    .disableOpenDir(); // 生成完不自动打开文件夹
                        })
                .packageConfig(
                        builder -> {
                            builder.parent(PARENT_PACKAGE)
                                    .entity("models.entity")
                                    .controller("controller")
                                    .mapper("mapper")
                                    .service("service")
                                    .serviceImpl("service.impl")
                                    // 3. 关键改动：使用 pathInfo 强行将 XML 导出到 resources 目录
                                    .pathInfo(
                                            Collections.singletonMap(
                                                    OutputFile.xml,
                                                    PROJECT_PATH + "/src/main/resources/mappers"));
                        })
                .strategyConfig(
                        builder -> {
                            builder.addInclude(TABLE_NAME)
                                    .entityBuilder()
                                    .enableLombok()
                                    .enableTableFieldAnnotation()
                                    .controllerBuilder()
                                    .enableRestStyle()
                                    .controllerBuilder()
                                    .superClass("com.jasper.springDemo.base.BaseController");
                        })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();

        System.out.println("代码生成完成！输出路径：" + PROJECT_PATH);
    }
}
