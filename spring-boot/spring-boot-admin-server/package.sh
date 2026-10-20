#!/usr/bin/env bash

set -e

# 当前子项目目录
PROJECT_DIR="$(pwd)"
PROJECT_NAME="spring-boot-admin-server"
IMAGE_NAME="${PROJECT_NAME}:1.0.0"

# 返回父目录，调用根目录的 Gradle Wrapper
cd ../../
./gradlew clean :spring-boot:spring-boot-admin-server:bootJar

# 回到子项目目录
cd "$PROJECT_DIR"

# 构建 Docker 镜像
# 使用 Dockerfile 中的指令和指定的内容（上下文）来自动构建一个 Docker 镜像（Image） -t指定标签
docker build -t "$IMAGE_NAME" .

# 删除旧容器（如果存在） || true 不要影响后面代码执行 如果这里没有删除失败的话
docker rm -f "$PROJECT_NAME" 2>/dev/null || true

# 启动新容器
docker run -d \
  --name "$PROJECT_NAME" \
  --memory=1g \
  -p 18080:18080 \
  -e JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC -Dfile.encoding=UTF-8" \
  "$IMAGE_NAME"

echo "部署完成：$PROJECT_NAME"
