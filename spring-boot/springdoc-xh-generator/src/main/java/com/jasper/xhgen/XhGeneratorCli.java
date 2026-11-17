package com.jasper.xhgen;

import com.jasper.xhgen.ApiModel.ParsedSpec;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

/**
 * 根据 springdoc 的 OpenAPI JSON 生成 xh 脚本。
 *
 * <pre>
 * ./gradlew :spring-boot:springdoc-xh-generator:run --args="--url http://localhost:18090/v3/api-docs -o ./xh-requests"
 * ./gradlew :spring-boot:springdoc-xh-generator:run --args="-f ./api-docs.json -o ./xh-requests --base-url http://host:port"
 * java -jar ... --file api-docs.json --dry-run
 * </pre>
 */
public final class XhGeneratorCli {

    private static final String DEFAULT_URL = "http://localhost:18091/v3/api-docs";
    private static final String DEFAULT_OUT = "xh-requests";

    public static void main(String[] args) {
        try {
            Config config = Config.parse(args);
            if (config.help()) {
                Config.usage();
                return;
            }

            String json = loadJson(config);
            ObjectMapper mapper = JsonMapper.builder().build();
            ParsedSpec spec = new OpenApiParser(mapper).parse(json, config.baseUrl());
            if (spec.baseUrl().isEmpty()) {
                spec = new ParsedSpec(spec.groups(), spec.schemes(), originOf(config.url()));
            }

            Path outDir = Path.of(config.out()).toAbsolutePath().normalize();
            int count = new ScriptRenderer(config.dryRun()).render(spec, outDir);

            System.out.printf("已解析 %d 个分组 / %d 个接口，%s %d 个文件，输出目录: %s%n",
                    spec.groups().size(),
                    spec.groups().values().stream().mapToInt(List::size).sum(),
                    config.dryRun() ? "计划生成" : "生成",
                    count,
                    outDir);
        } catch (IllegalArgumentException e) {
            System.err.println("参数错误: " + e.getMessage());
            Config.usage();
            System.exit(2);
        } catch (Exception e) {
            System.err.println("生成失败: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private static String loadJson(Config config) throws IOException, InterruptedException {
        if (config.file() != null) {
            Path path = Path.of(config.file());
            if (!Files.isReadable(path)) {
                throw new IOException("找不到或无法读取 OpenAPI 文件: " + path);
            }
            return Files.readString(path, StandardCharsets.UTF_8);
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(config.url()))
                .timeout(Duration.ofSeconds(15))
                .header("Accept", "application/json")
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() / 100 != 2) {
            throw new IOException("拉取 OpenAPI 文档失败: HTTP " + response.statusCode() + " " + config.url());
        }
        return response.body();
    }

    private static String originOf(String url) {
        try {
            URI uri = URI.create(url);
            int port = uri.getPort();
            if (port < 0) {
                port = "https".equalsIgnoreCase(uri.getScheme()) ? 443 : 80;
            }
            return uri.getScheme() + "://" + uri.getHost() + ":" + port;
        } catch (Exception ignored) {
            return "http://localhost:8080";
        }
    }

    /** 极简 CLI 参数解析，不引入 picocli。 */
    private record Config(
            String url,
            String file,
            String out,
            String baseUrl,
            boolean dryRun,
            boolean help
    ) {

        static Config parse(String[] args) {
            String url = DEFAULT_URL;
            String file = null;
            String out = DEFAULT_OUT;
            String baseUrl = null;
            boolean dryRun = false;
            boolean help = false;

            for (int i = 0; i < args.length; i++) {
                String arg = args[i];
                switch (arg) {
                    case "-h", "--help" -> help = true;
                    case "--dry-run" -> dryRun = true;
                    case "-u", "--url" -> url = next(args, ++i, arg);
                    case "-f", "--file" -> file = next(args, ++i, arg);
                    case "-o", "--out" -> out = next(args, ++i, arg);
                    case "-b", "--base-url" -> baseUrl = next(args, ++i, arg);
                    default -> throw new IllegalArgumentException("未知参数: " + arg);
                }
            }
            return new Config(url, file, out, baseUrl, dryRun, help);
        }

        private static String next(String[] args, int index, String flag) {
            if (index >= args.length) {
                throw new IllegalArgumentException(flag + " 缺少参数值");
            }
            return args[index];
        }

        static void usage() {
            System.out.printf("""
                    用法: springdoc-xh-generator [选项]
                    
                      -u, --url <url>       springdoc OpenAPI JSON 地址（默认 %s）
                      -f, --file <path>     本地 OpenAPI JSON 文件（与 --url 二选一）
                      -o, --out <dir>       输出目录（默认 ./%s）
                      -b, --base-url <url>  覆盖脚本里的 BASE_URL（默认取 spec.servers[0]）
                          --dry-run         只打印将要生成的文件
                      -h, --help            显示帮助
                    
                    示例:
                      ./gradlew :spring-boot:springdoc-xh-generator:run --args="--file ./api-docs.json -o ./xh-requests"
                      java -jar build/libs/springdoc-xh-generator.jar --url http://localhost:18090/v3/api-docs
                    %n""", DEFAULT_URL, DEFAULT_OUT);
        }
    }
}
