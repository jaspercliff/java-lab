package com.jasper.xhgen;

import com.jasper.xhgen.ApiModel.ApiArg;
import com.jasper.xhgen.ApiModel.ApiOperation;
import com.jasper.xhgen.ApiModel.ParsedSpec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 把 {@link ParsedSpec} 渲染成目录 + bash 脚本。
 *
 * <p>生成的脚本刻意保持「打开就能看懂」：每个请求就是一段直接的 {@code xh} 命令，
 * 参数内联成 {@code key==${1-default}}；没有 local 变量、数组拼装、鉴权分支。
 */
final class ScriptRenderer {

    private static final Set<PosixFilePermission> EXECUTABLE = Set.of(
            PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE,
            PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_EXECUTE,
            PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_EXECUTE
    );

    private final boolean dryRun;

    ScriptRenderer(boolean dryRun) {
        this.dryRun = dryRun;
    }

    /** @return 生成（或 dry-run 计划）的文件数量 */
    int render(ParsedSpec spec, Path outDir) throws IOException {
        List<RenderedFile> files = new ArrayList<>();
        files.add(new RenderedFile(outDir.resolve("README.md"), readme(spec)));

        for (Map.Entry<String, List<ApiOperation>> group : spec.groups().entrySet()) {
            Path groupDir = outDir.resolve(Names.dir(group.getKey()));
            List<ApiOperation> queries = new ArrayList<>();
            for (ApiOperation operation : group.getValue()) {
                if ("GET".equals(operation.method())) {
                    queries.add(operation);
                } else {
                    files.add(new RenderedFile(
                            groupDir.resolve(scriptName(operation)),
                            singleScript(operation, group.getKey(), spec.baseUrl())
                    ));
                }
            }
            if (!queries.isEmpty()) {
                files.add(new RenderedFile(
                        groupDir.resolve("queries.sh"),
                        queryScript(queries, group.getKey(), spec.baseUrl())
                ));
            }
        }

        for (RenderedFile file : files) {
            if (dryRun) {
                System.out.println("[dry-run] " + file.path());
                continue;
            }
            Files.createDirectories(file.path().getParent());
            Files.writeString(file.path(), file.content(), StandardCharsets.UTF_8);
            if (file.path().getFileName().toString().endsWith(".sh")) {
                try {
                    Files.setPosixFilePermissions(file.path(), EXECUTABLE);
                } catch (UnsupportedOperationException ignored) {
                    // Windows 文件系统没有 POSIX 权限，忽略
                }
            }
        }
        return files.size();
    }

    // --------------------------------------------------------------- 查询集中脚本

    private String queryScript(List<ApiOperation> operations, String group, String baseUrl) {
        StringBuilder sb = new StringBuilder();
        header(sb, group, "查询集，① 菜单里可输入 '2 1003' 带参  ② ./queries.sh 1 1003  ③ ./queries.sh listBooks spring");

        Map<String, String> fnByOp = new LinkedHashMap<>();
        Set<String> used = Names.orderedSet();
        for (ApiOperation op : operations) {
            fnByOp.put(op.id(), Names.shellIdentifier(op.id(), used));
        }

        sb.append("\n").append(baseUrlLine(baseUrl)).append("\n");

        for (ApiOperation op : operations) {
            String fn = fnByOp.get(op.id());
            sb.append("\n# ").append(Names.comment(op.summary())).append("\n")
                    .append("# ").append(op.method()).append(" ").append(op.path()).append("\n")
                    .append(positionComment(op)).append(fn).append("() {\n");
            command(sb, op, "  ");
            sb.append("}\n");
        }

        sb.append("\n# ---------------------------------------------------- 选择执行 / 命令行执行\n")
                .append("FUNCS=(");
        for (ApiOperation op : operations) {
            sb.append(Names.shq(fnByOp.get(op.id()))).append(" ");
        }
        sb.append(")\n")
                .append("if [[ $# -eq 0 ]]; then\n")
                .append("  printf '选择查询:\\n'\n");
        int i = 1;
        for (ApiOperation op : operations) {
            sb.append("  printf '  %d) %-24s %s\\n' ").append(i++)
                    .append(" ").append(Names.shq(fnByOp.get(op.id())))
                    .append(" ").append(Names.shq(Names.comment(op.summary()))).append("\n");
        }
        sb                .append("  printf '序号/函数名 + 参数 (q 退出): '\n")
                .append("  IFS= read -r line || exit 0\n")
                .append("  [[ -z \"$line\" || \"$line\" == q || \"$line\" == Q ]] && exit 0\n")
                .append("  read -r -a sel <<< \"$line\"\n")
                .append("  set -- \"${sel[@]}\"\n")
                .append("fi\n")
                .append("first=\"${1:-}\"\n")
                .append("if [[ \"$first\" =~ ^[0-9]+$ ]]; then\n")
                .append("  i=$((first - 1)); shift\n")
                .append("  [[ $i -ge 0 && $i -lt ").append(operations.size()).append(" ]] || { printf '无效序号: %s\\n' \"$first\" >&2; exit 2; }\n")
                .append("  \"${FUNCS[$i]}\" \"$@\"\n")
                .append("else\n")
                .append("  [[ $# -gt 0 ]] && shift\n")
                .append("  \"$first\" \"$@\"\n")
                .append("fi\n");
        return sb.toString();
    }

    // --------------------------------------------------------------- 单接口脚本

    private String singleScript(ApiOperation op, String group, String baseUrl) {
        StringBuilder sb = new StringBuilder();
        header(sb, group, Names.comment(op.summary()));
        sb.append("\n").append(baseUrlLine(baseUrl)).append("\n")
                .append("\n# ").append(op.method()).append(" ").append(op.path()).append("\n")
                .append(positionComment(op));
        command(sb, op, "");
        return sb.toString();
    }

    /**
     * 生成单个请求的 xh 命令。路径参数 / query / header / cookie / form 字段内联
     * {@code ${N-default}} 支持位置覆盖；整块 JSON body 写死交给 jaq。
     */
    private void command(StringBuilder sb, ApiOperation op, String indent) {
        int position = 1;
        StringBuilder items = new StringBuilder();
        String bodyJson = null;
        String rawBody = null;

        for (ApiArg arg : op.args()) {
            String template = arg.itemTemplate();
            if ("@json".equals(template)) {
                bodyJson = arg.defaultValue();
                continue;
            }
            if (template == null) {
                rawBody = arg.defaultValue();
                continue;
            }
            String expr = "${" + position++ + "-" + inlineDefault(arg.defaultValue()) + "}";
            if ("@path".equals(template)) {
                continue; // 路径参数只替换 URL，不生成 xh 项
            }
            String item = template.replace("{v}", expr);
            items.append(" \\\n").append(indent).append("  ").append(Names.dqReady(item));
        }

        String url = "${BASE_URL}" + substitutePath(op.path(), op.args());
        String prefix = "xh";
        if (!op.bodyFlag().isEmpty() && !"--raw".equals(op.bodyFlag())) {
            prefix += " " + op.bodyFlag();
        }
        prefix += " " + op.method();

        if (bodyJson != null) {
            sb.append(indent).append("BODY=$(jaq -n ").append(Names.shq(bodyJson)).append(")\n\n");
            sb.append(indent).append("printf '%s' \"$BODY\" | ")
                    .append(prefix).append(" ").append(Names.dqReady(url))
                    .append(" Content-Type:application/json");
            sb.append(items);
            sb.append("\n");
        } else if (rawBody != null) {
            sb.append(indent).append("printf '%s' ").append(Names.shq(rawBody)).append(" | ")
                    .append("xh --raw ").append(op.method()).append(" ").append(Names.dqReady(url));
            sb.append(items);
            sb.append("\n");
        } else {
            sb.append(indent).append(prefix).append(" ").append(Names.dqReady(url));
            sb.append(items);
            sb.append("\n");
        }
    }

    /** 把 path 里的 {name} 换成对应路径参数的 ${N-default}。 */
    private String substitutePath(String path, List<ApiArg> args) {
        String result = path;
        int position = 1;
        for (ApiArg arg : args) {
            String template = arg.itemTemplate();
            if ("@json".equals(template) || template == null) {
                continue;
            }
            if ("@path".equals(template)) {
                String expr = "${" + position + "-" + inlineDefault(arg.defaultValue()) + "}";
                result = result.replace("{" + arg.name() + "}", expr);
            }
            position++;
        }
        return result;
    }

    /** 位置参数说明注释：只列占位置的项（body 写死不占位置）。 */
    private String positionComment(ApiOperation op) {
        StringBuilder sb = new StringBuilder();
        int position = 1;
        for (ApiArg arg : op.args()) {
            String template = arg.itemTemplate();
            if ("@json".equals(template) || template == null) {
                continue;
            }
            sb.append("# $").append(position++).append("=").append(Names.comment(arg.comment())).append("\n");
        }
        return sb.toString();
    }

    /** 默认值放进双引号内的 ${N-...}：转义会破坏 shell 语法的字符，换行压成空格。 */
    private String inlineDefault(String value) {
        String v = value == null ? "" : value;
        return v.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("`", "\\`")
                .replace("$", "\\$")
                .replaceAll("[\\r\\n]+", " ");
    }

    // --------------------------------------------------------------- 公共片段

    private void header(StringBuilder sb, String group, String description) {
        sb.append("#!/usr/bin/env bash\n")
                .append("# 由 springdoc-xh-generator 生成，重新生成会覆盖本文件\n")
                .append("# Controller: ").append(Names.comment(group)).append("\n")
                .append("# ").append(Names.comment(description)).append("\n");
    }

    private String baseUrlLine(String baseUrl) {
        return "BASE_URL=\"${BASE_URL:-" + Names.dqContent(baseUrl) + "}\"\n" +
                "BASE_URL=\"${BASE_URL%/}\"";
    }

    private String scriptName(ApiOperation operation) {
        String stem = Names.fileStem(operation.id());
        return operation.method().toLowerCase() + "-" + stem + ".sh";
    }

    private String readme(ParsedSpec spec) {
        StringBuilder sb = new StringBuilder();
        sb.append("# xh 请求脚本（由 springdoc-xh-generator 自动生成）\n\n")
                .append("一个 Controller / tag 一个目录。目录内 GET 合并到 `queries.sh`，")
                .append("其余每个接口一个 `post-*.sh` / `put-*.sh` …\n\n")
                .append("## 查询脚本 queries.sh\n\n")
                .append("```bash\n")
                .append("./queries.sh                 # 菜单选择；提示处可输入 \"2 1003\"（序号 + 参数）\n")
                .append("./queries.sh 1               # 按序号，用脚本里的默认参数\n")
                .append("./queries.sh 1 1003          # 按序号 + 位置参数\n")
                .append("./queries.sh listBooks spring published 0 20   # 按函数名 + 位置参数\n")
                .append("```\n\n")
                .append("## 维护脚本\n\n")
                .append("打开即见完整的 `xh` 命令；JSON 请求体由 `jaq -n` 生成（依赖 jaq）。\n")
                .append("带位置参数的直接跟在脚本名后，例如 `./post-createBook.sh 'My Book'`。\n")
                .append("用 `BASE_URL=http://host:port ./xxx.sh` 可临时改目标地址。\n\n")
                .append("## 分组\n\n");
        for (String group : spec.groups().keySet()) {
            sb.append("- `").append(Names.dir(group)).append("`\n");
        }
        sb.append("\n> 重新生成会覆盖本目录下所有脚本；自定义改动请另存。\n");
        return sb.toString();
    }

    private record RenderedFile(Path path, String content) {
    }
}
