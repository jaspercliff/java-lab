# springdoc-xh-generator

读取 springdoc 输出的 OpenAPI JSON（`/v3/api-docs`），生成可直接用 [xh](https://github.com/ducaale/xh)
执行的 bash 脚本。生成的脚本刻意保持**简洁**：打开就是一段看得懂的 `xh` 命令，没有层层封装。

## 生成规则

- **一个 Controller / tag → 一个目录**（用 OpenAPI `tags[0]` 归类；未写 tag 时用 `default`）。
- **一个 Controller 下所有 GET 查询 → 一个 `queries.sh`**，每个查询是一个只含 `xh` 命令的函数：
  - `./queries.sh` 交互菜单：提示处可输入 `2 1003`（序号 + 参数）或 `getBookById 1003`；
  - `./queries.sh 1 1001` 按序号 + 位置参数；
  - `./queries.sh getBookById 1001` 按 operationId + 位置参数。
- **维护请求（POST/PUT/DELETE/PATCH...）→ 一个接口一个 `<method>-<operationId>.sh`**，
  打开即是完整命令，直接 `./post-createBook.sh [参数...]` 执行。脚本头部注释列出了位置参数顺序。
- 参数内联成 `key==${1-spring}` / `Header:${2-lab}`:不传用默认值，直接跟在脚本名后即覆盖；
  显式传 `''` 传空值。
- **JSON 请求体整块写死**，用 `jaq -n '{...}'` 生成（依赖 [jaq](https://github.com/01mf02/jaq)，
  数字/布尔/数组/对象类型天然正确），通过 stdin 发给 `xh`。改请求体直接编辑脚本即可。
- form/multipart 仍是逐字段的 xh 项；非对象 body 用 `--raw` 走 stdin。

## 用法

```bash
# 从运行中的 springdoc 应用拉取（默认 http://localhost:18090/v3/api-docs）
./gradlew :spring-boot:springdoc-xh-generator:run --args="-o ./xh-requests"

# 从本地 OpenAPI JSON 生成，并覆盖脚本 BASE_URL
./gradlew :spring-boot:springdoc-xh-generator:run --args="-f ./api-docs.json -o ./xh-requests -b http://localhost:18090"

# 只看会生成哪些文件
./gradlew :spring-boot:springdoc-xh-generator:run --args="-f ./api-docs.json --dry-run"

# 打成可执行 CLI
./gradlew :spring-boot:springdoc-xh-generator:installDist
./spring-boot/springdoc-xh-generator/build/install/springdoc-xh-generator/bin/springdoc-xh-generator --help
```

### 参数

| 参数 | 说明 |
| --- | --- |
| `-u, --url <url>` | OpenAPI JSON 地址，默认 `http://localhost:18090/v3/api-docs` |
| `-f, --file <path>` | 本地 OpenAPI JSON 文件（优先于 `--url`） |
| `-o, --out <dir>` | 输出目录，默认 `./xh-requests` |
| `-b, --base-url <url>` | 覆盖脚本里的 `BASE_URL`，默认取 `servers[0].url`（会展开 `{变量}`） |
| `--dry-run` | 只打印计划，不写文件 |
| `-h, --help` | 帮助 |

## 生成的脚本长什么样

查询脚本（`queries.sh`，节选）：

```bash
BASE_URL="${BASE_URL:-http://localhost:18090}"
BASE_URL="${BASE_URL%/}"

# 按 ID 查询书籍
# GET /api/knowledge/books/{id}
# $1=路径参数 id
getBookById() {
  xh GET "${BASE_URL}/api/knowledge/books/${1-1001}"
}
```

维护脚本（`post-createBook.sh`，节选）：

```bash
BODY=$(jaq -n '{
  "title" : "Springdoc in Action",
  "price" : 88.5,
  "stock" : 42
}')

printf '%s' "$BODY" | xh POST "${BASE_URL}/api/knowledge/books" Content-Type:application/json
```

临时换目标地址：`BASE_URL=http://host:port ./xxx.sh`。

## 实现要点

- Jackson 3（`tools.jackson.databind`）：`stringValue()` 是严格方法，非文本节点会抛异常，
  取值前必须先判 `isTextual()`；从 schema 造样例时数字/布尔要保型（`numberValue()` / `booleanValue()`），
  否则 `valueToTree` 会把它们写成字符串。
- 支持 OpenAPI 3.1 的 `"type": ["string", "null"]`。
- 默认值内联成 `${N-default}`，已按双引号内规则转义 `$` / `` ` `` / `"` / `\`；
  用单个 `-`（不是 `:-`）让显式 `''` 能覆盖默认值。
- 不用 `set -u`，兼容 macOS 自带 bash 3.2 的空数组 / 位置参数展开。
