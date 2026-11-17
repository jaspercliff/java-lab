package com.jasper.xhgen;

import com.jasper.xhgen.ApiModel.ApiArg;
import com.jasper.xhgen.ApiModel.ApiOperation;
import com.jasper.xhgen.ApiModel.ParsedSpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScriptRendererTest {

    @TempDir
    Path tempDir;

    @Test
    void rendersOneDirectoryPerGroupAndAggregatesGets() throws IOException {
        List<ApiOperation> gets = List.of(
                op("listBooks", "GET", "/api/books", "分页查询",
                        List.of(
                                new ApiArg("page", "0", "page=={v}", "query page"),
                                new ApiArg("sort", "title,asc", "sort=={v}", "query sort")
                        ), ""),
                op("getBookById", "GET", "/api/books/{id}", "按 ID 查询",
                        List.of(
                                new ApiArg("id", "1001", "@path", "路径参数 id"),
                                new ApiArg("X-Tenant", "lab", "X-Tenant:{v}", "header X-Tenant")
                        ), "")
        );
        List<ApiOperation> writes = List.of(
                op("createBook", "POST", "/api/books", "创建书籍",
                        List.of(
                                new ApiArg("X-Trace", "t1", "X-Trace:{v}", "header X-Trace"),
                                new ApiArg("body", "{\n  \"title\" : \"effective java\",\n  \"price\" : 88.5\n}",
                                        "@json", "body JSON")
                        ), "")
        );
        LinkedHashMap<String, List<ApiOperation>> groups = new LinkedHashMap<>();
        groups.put("01-查询与参数", gets);
        groups.put("02-请求体与校验", writes);

        int count = new ScriptRenderer(false).render(
                new ParsedSpec(groups, List.of("bearer"), "http://localhost:18090"), tempDir);

        assertThat(count).isEqualTo(3); // README + queries.sh + post-createBook.sh
        Path queryScript = tempDir.resolve("01-查询与参数").resolve("queries.sh");
        Path writeScript = tempDir.resolve("02-请求体与校验").resolve("post-createBook.sh");
        assertThat(queryScript).exists();
        assertThat(writeScript).exists();

        String query = Files.readString(queryScript);
        assertThat(query)
                .contains("#!/usr/bin/env bash")
                .contains("BASE_URL=\"${BASE_URL:-http://localhost:18090}\"")
                .contains("listBooks() {")
                .contains("getBookById() {")
                .contains("xh GET \"${BASE_URL}/api/books\"")
                .contains("\"page==${1-0}\"")
                .contains("\"sort==${2-title,asc}\"")
                .contains("xh GET \"${BASE_URL}/api/books/${1-1001}\"")
                .contains("\"X-Tenant:${2-lab}\"")
                .contains("FUNCS=('listBooks' 'getBookById'")
                .contains("read -r -a sel <<< \"$line\"")
                .contains("set -- \"${sel[@]}\"")
                .contains("[[ \"$first\" =~ ^[0-9]+$ ]]")
                .doesNotContain("local ")
                .doesNotContain("AUTH")
                .doesNotContain("items+=(");

        String write = Files.readString(writeScript);
        assertThat(write)
                .contains("xh POST \"${BASE_URL}/api/books\" Content-Type:application/json")
                .contains("BODY=$(jaq -n '{")
                .contains("\"title\" : \"effective java\"")
                .contains("printf '%s' \"$BODY\" |")
                .contains("\"X-Trace:${1-t1}\"")
                .doesNotContain("local ")
                .doesNotContain("items+=(");

        assertThat(Files.isExecutable(queryScript)).isTrue();
        assertThat(Files.readString(tempDir.resolve("README.md"))).contains("01-查询与参数");
    }

    @Test
    void dryRunDoesNotWriteFiles() throws IOException {
        LinkedHashMap<String, List<ApiOperation>> groups = new LinkedHashMap<>();
        groups.put("t", List.of(op("ping", "GET", "/ping", "", List.of(), "")));

        int count = new ScriptRenderer(true).render(
                new ParsedSpec(groups, List.of(), "http://localhost:8080"), tempDir);

        assertThat(count).isGreaterThan(0);
        try (var stream = Files.list(tempDir)) {
            assertThat(stream).isEmpty();
        }
    }

    @Test
    void rendersRawBodyViaStdin() throws IOException {
        LinkedHashMap<String, List<ApiOperation>> groups = new LinkedHashMap<>();
        groups.put("t", List.of(op("echo", "POST", "/echo", "",
                List.of(new ApiArg("body", "hello", null, "body 原始数据")), "--raw")));

        new ScriptRenderer(false).render(new ParsedSpec(groups, List.of(), "http://localhost:8080"), tempDir);
        String script = Files.readString(tempDir.resolve("t").resolve("post-echo.sh"));
        assertThat(script)
                .contains("xh --raw POST")
                .contains("printf '%s' 'hello' |");
    }

    @Test
    void inlineDefaultEscapesShellMetaCharacters() throws IOException {
        LinkedHashMap<String, List<ApiOperation>> groups = new LinkedHashMap<>();
        groups.put("t", List.of(op("search", "GET", "/search", "",
                List.of(
                        new ApiArg("q", "a$b", "q=={v}", "query q"),
                        new ApiArg("name", "it's ok", "name=={v}", "query name")
                ), "")));

        new ScriptRenderer(false).render(new ParsedSpec(groups, List.of(), "http://localhost:8080"), tempDir);
        String script = Files.readString(tempDir.resolve("t").resolve("queries.sh"));
        assertThat(script)
                .contains("\"q==${1-a\\$b}\"")
                .contains("\"name==${2-it's ok}\"");
    }

    private static ApiOperation op(
            String id, String method, String path, String summary, List<ApiArg> args, String bodyFlag
    ) {
        return new ApiOperation(id, method, path, summary, args, bodyFlag, false);
    }
}
