package com.jasper.xhgen;

import com.jasper.xhgen.ApiModel.ApiArg;
import com.jasper.xhgen.ApiModel.ApiOperation;
import com.jasper.xhgen.ApiModel.ParsedSpec;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiParserTest {

    private final ObjectMapper mapper = JsonMapper.builder().build();
    private final OpenApiParser parser = new OpenApiParser(mapper);

    private static final String SPEC = """
            {
              "openapi": "3.0.1",
              "servers": [{"url": "http://localhost:18090"}],
              "paths": {
                "/api/books": {
                  "get": {
                    "operationId": "listBooks",
                    "summary": "分页查询书籍",
                    "tags": ["01-查询与参数"],
                    "parameters": [
                      {"name": "page", "in": "query", "schema": {"type": "integer", "default": 0}},
                      {"$ref": "#/components/parameters/sortParam"}
                    ]
                  },
                  "post": {
                    "operationId": "createBook",
                    "summary": "创建书籍",
                    "tags": ["02-请求体与校验"],
                    "requestBody": {
                      "content": {
                        "application/json": {
                          "schema": {"$ref": "#/components/schemas/BookCreate"},
                          "example": {"title": "effective java", "price": 88.5}
                        }
                      }
                    }
                  }
                },
                "/api/books/{id}": {
                  "parameters": [
                    {"name": "id", "in": "path", "required": true, "schema": {"type": "integer"}, "example": 1001}
                  ],
                  "get": {
                    "operationId": "getBookById",
                    "summary": "按 ID 查询",
                    "tags": ["01-查询与参数"]
                  }
                }
              },
              "components": {
                "parameters": {
                  "sortParam": {"name": "sort", "in": "query", "schema": {"type": "string", "default": "title,asc"}}
                },
                "schemas": {
                  "BookCreate": {
                    "type": "object",
                    "properties": {
                      "title": {"type": "string"},
                      "price": {"type": "number"}
                    }
                  }
                },
                "securitySchemes": {
                  "bearerAuth": {"type": "http", "scheme": "bearer"}
                }
              }
            }
            """;

    @Test
    void groupsByTagAndKeepsPathLevelParameters() {
        ParsedSpec spec = parser.parse(SPEC, null);

        assertThat(spec.groups()).containsOnlyKeys("01-查询与参数", "02-请求体与校验");
        assertThat(spec.groups().get("01-查询与参数")).hasSize(2);
        assertThat(spec.groups().get("02-请求体与校验")).hasSize(1);
        assertThat(spec.baseUrl()).isEqualTo("http://localhost:18090");
        assertThat(spec.schemes()).containsExactly("bearer");

        ApiOperation getById = spec.groups().get("01-查询与参数").stream()
                .filter(op -> op.id().equals("getBookById"))
                .findFirst().orElseThrow();
        assertThat(getById.path()).isEqualTo("/api/books/{id}");
        assertThat(getById.args()).extracting(ApiArg::itemTemplate).containsExactly("@path");
        assertThat(getById.args()).extracting(a -> a.name()).containsExactly("id");
    }

    @Test
    void resolvesRefsAndBuildsQueryItems() {
        ParsedSpec spec = parser.parse(SPEC, null);
        ApiOperation list = spec.groups().get("01-查询与参数").get(0);

        assertThat(list.args()).hasSize(2);
        assertThat(list.args().get(0).itemTemplate()).isEqualTo("page=={v}");
        assertThat(list.args().get(0).defaultValue()).isEqualTo("0");
        assertThat(list.args().get(1).itemTemplate()).isEqualTo("sort=={v}");
        assertThat(list.args().get(1).defaultValue()).isEqualTo("title,asc");
    }

    @Test
    void buildsWholeJsonBodyFromExample() {
        ParsedSpec spec = parser.parse(SPEC, null);
        ApiOperation create = spec.groups().get("02-请求体与校验").get(0);

        assertThat(create.method()).isEqualTo("POST");
        assertThat(create.args()).hasSize(1);
        ApiArg body = create.args().get(0);
        assertThat(body.itemTemplate()).isEqualTo("@json");
        assertThat(body.defaultValue())
                .contains("\"title\"")
                .contains("effective java")
                .contains("\"price\"")
                .contains("88.5");
    }

    @Test
    void keepsNumberAndBooleanTypesInJsonBody() {
        String json = """
                {"paths": {"/x": {"post": {
                  "operationId": "create", "tags": ["t"],
                  "requestBody": {"content": {"application/json": {
                    "schema": {"type": "object", "properties": {
                      "price": {"type": "number", "example": 88.5},
                      "stock": {"type": "integer", "example": 42},
                      "flag": {"type": "boolean", "example": true},
                      "title": {"type": "string", "example": "x"}
                    }}
                  }}}
                }}}}
                """;
        ApiOperation create = parser.parse(json, null).groups().get("t").get(0);
        String body = create.args().get(0).defaultValue();
        assertThat(body)
                .contains("\"price\" : 88.5")
                .contains("\"stock\" : 42")
                .contains("\"flag\" : true")
                .contains("\"title\" : \"x\"");
    }

    @Test
    void expandsServerVariables() {
        String json = """
                {"servers": [{"url": "http://localhost:{port}/{basePath}", "variables": {
                   "port": {"default": "18090"},
                   "basePath": {"description": "no default"}
                }}], "paths": {}}
                """;
        assertThat(parser.parse(json, null).baseUrl()).isEqualTo("http://localhost:18090");
    }

    @Test
    void baseUrlOverrideWins() {
        ParsedSpec spec = parser.parse(SPEC, "https://prod.example.com/");
        assertThat(spec.baseUrl()).isEqualTo("https://prod.example.com");
    }

    @Test
    void derivesOperationIdWhenMissing() {
        String json = """
                {"paths": {"/ping": {"get": {"tags": ["util"]}}}}
                """;
        ParsedSpec spec = parser.parse(json, null);
        ApiOperation op = spec.groups().get("util").get(0);
        assertThat(op.id()).isEqualTo("get_ping");
        assertThat(op.args()).isEmpty();
    }

    @Test
    void usesRawBodyForScalarRequestBody() {
        String json = """
                {"paths": {"/echo": {"post": {
                  "operationId": "echo", "tags": ["t"],
                  "requestBody": {"content": {"text/plain": {"example": "hello"}}}
                }}}}
                """;
        ParsedSpec spec = parser.parse(json, null);
        ApiOperation op = spec.groups().get("t").get(0);
        assertThat(op.args()).hasSize(1);
        assertThat(op.args().get(0).itemTemplate()).isNull();
        assertThat(op.args().get(0).defaultValue()).isEqualTo("hello");
    }

    @Test
    void shellIdentifierIsUniqueAndSafe() {
        Set<String> used = Names.orderedSet();
        assertThat(Names.shellIdentifier("list-books", used)).isEqualTo("list_books");
        assertThat(Names.shellIdentifier("list books", used)).isEqualTo("list_books_2");
        assertThat(Names.shellIdentifier("123", used)).isEqualTo("op_123");
        assertThat(Names.shellIdentifier("if", used)).isEqualTo("if_2");
    }

    @Test
    void shqHandlesQuotesAndDollar() {
        assertThat(Names.shq("it's $HOME")).isEqualTo("'it'\\''s $HOME'");
    }

    @Test
    void dqKeepsVariablesButEscapesQuotes() {
        assertThat(Names.dqKeepVars("page==${query_page}"))
                .isEqualTo("\"page==${query_page}\"");
        assertThat(Names.dqKeepVars("say \"hi\""))
                .isEqualTo("\"say \\\"hi\\\"\"");
    }

    @Test
    void collectsMultipleSchemes() {
        String json = """
                {"paths": {}, "components": {"securitySchemes": {
                  "bearerAuth": {"type": "http", "scheme": "bearer"},
                  "key": {"type": "apiKey", "name": "X-Key", "in": "header"},
                  "oauth": {"type": "oauth2", "flows": {}}
                }}}
                """;
        assertThat(parser.parse(json, null).schemes())
                .containsExactlyInAnyOrder("bearer", "apiKey", "oauth2");
    }
}
