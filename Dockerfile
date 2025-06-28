# Build frontend
FROM node:16 AS frontend-build
WORKDIR /app
COPY file-sharing-ui/package*.json ./
RUN npm install
COPY file-sharing-ui/ ./
RUN npm run build

# Build backend
FROM gradle:8.14.2-jdk21 AS backend-build
WORKDIR /app
COPY . ./
RUN gradle build -x test --no-daemon --build-cache \
    -Porg.gradle.java.installations.auto-download=true

# Final image
FROM eclipse-temurin:21-jre
WORKDIR /app

# 创建数据库和配置文件目录
RUN mkdir -p /app/data /app/config

# 复制应用文件
COPY --from=backend-build /app/build/libs/*.jar app.jar
COPY --from=frontend-build /app/dist /app/static

# 设置配置文件
COPY src/main/resources/application.properties /app/config/

# 设置环境变量默认值
ENV DB_URL=jdbc:h2:file:/app/data/filedb
ENV DB_USERNAME=sa
ENV DB_PASSWORD=1qaz!QAZ
ENV SERVER_PORT=8080
ENV MAX_FILE_SIZE=200MB
ENV MAX_REQUEST_SIZE=200MB
ENV APP_MAX_FILE_SIZE=200
ENV DDL_AUTO=update
ENV H2_CONSOLE_ENABLED=false

# 暴露数据库目录和配置文件目录
VOLUME ["/app/data", "/app/config"]

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar", \
           "--spring.config.location=file:/app/config/application.properties", \
           "--spring.datasource.url=${DB_URL}", \
           "--spring.datasource.username=${DB_USERNAME}", \
           "--spring.datasource.password=${DB_PASSWORD}", \
           "--server.port=${SERVER_PORT}", \
           "--spring.servlet.multipart.max-file-size=${MAX_FILE_SIZE}", \
           "--spring.servlet.multipart.max-request-size=${MAX_REQUEST_SIZE}", \
           "--spring.jpa.hibernate.ddl-auto=${DDL_AUTO}", \
           "--app.upload.max-file-size=${APP_MAX_FILE_SIZE}", \
           "--spring.h2.console.enabled=${H2_CONSOLE_ENABLED}"]
