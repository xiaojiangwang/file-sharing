# 文件分享系统 (File Sharing System)

## 项目背景

文件分享系统是一个基于Spring Boot和前端技术栈开发的全栈Web应用，旨在提供安全、便捷的文件和文本分享服务。用户可以上传文件或文本内容，系统会生成唯一的分享链接，支持密码保护功能，确保分享内容的安全性。

## 项目架构

- **后端**: Spring Boot 3.5.3 + Spring Security + Spring Data JPA
- **数据库**: H2 Database (嵌入式数据库)
- **前端**: file-sharing-ui (独立前端工程)
- **部署**: Docker容器化部署

## 主要功能

### 文件分享功能
- ✅ 文件上传
- ✅ 文件下载
- ✅ 文件密码保护
- ✅ 文件备注信息
- ✅ 文件列表查看
- ✅ 文件删除
- ✅ 文件大小限制配置

### 文本分享功能
- ✅ 文本内容上传
- ✅ 文本内容查看
- ✅ 文本密码保护
- ✅ 文本备注信息
- ✅ 文本列表查看
- ✅ 文本删除

### 安全特性
- ✅ 跨域请求支持 (CORS)
- ✅ 密码保护机制
- ✅ 文件大小限制
- ✅ Spring Security集成

## 技术栈

### 后端技术
- **框架**: Spring Boot 3.5.3
- **安全**: Spring Security
- **数据访问**: Spring Data JPA
- **数据库**: H2 Database
- **构建工具**: Gradle 8.14.2
- **Java版本**: JDK 21+

### 依赖库
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-security'
    runtimeOnly 'com.h2database:h2'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

## 本地开发环境搭建

### 前置要求
- JDK 21 或更高版本
- Gradle 8.14.2 或更高版本
- Node.js 16+ (用于前端开发)

### 后端启动步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd file-sharing
```

2. **配置数据库**
数据库配置文件位于 `src/main/resources/application.properties`:
```properties
# H2数据库配置
spring.datasource.url=jdbc:h2:file:./data/filedb
spring.datasource.username=sa
spring.datasource.password=1qaz!QAZ

# H2控制台访问
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

3. **构建项目**
```bash
# Windows
.\gradlew build

# Linux/Mac
./gradlew build
```

4. **启动应用**
```bash
# Windows
.\gradlew bootRun

# Linux/Mac
./gradlew bootRun
```

5. **访问应用**
- 应用地址: http://localhost:8080
- H2数据库控制台: http://localhost:8080/h2-console
- API文档: http://localhost:8080/api/

### 前端启动步骤

1. **进入前端目录**
```bash
cd ../file-sharing-ui
```

2. **安装依赖**
```bash
npm install
```

3. **启动开发服务器**
```bash
npm run dev
```

4. **访问前端应用**
- 前端地址: http://localhost:5173 (或其他端口，根据前端配置)

## Docker部署

### 构建和运行

1. **构建Docker镜像**
```bash
docker build -t file-sharing .
```

2. **运行容器**
```bash
docker run -d \
  --name file-sharing \
  -p 8080:8080 \
  -v $(pwd)/data:/app/data \
  -v $(pwd)/config:/app/config \
  -e DB_URL=jdbc:h2:file:/app/data/filedb \
  -e DB_USERNAME=sa \
  -e DB_PASSWORD='1qaz!QAZ' \
  -e SERVER_PORT=8080 \
  -e MAX_FILE_SIZE=200MB \
  -e MAX_REQUEST_SIZE=200MB \
  -e APP_MAX_FILE_SIZE=200 \
  -e DDL_AUTO=update \
  wangxiaojiang10088/file-sharing:latest
```

### Docker Compose部署

创建 `docker-compose.yml` 文件:
```yaml
version: '3.8'
services:
  file-sharing:
    image: wangxiaojiang10088/file-sharing:latest
    build: .
    ports:
      - "8080:8080"
    volumes:
      - ./data:/app/data
      - ./config:/app/config
    environment:
      - DB_URL=jdbc:h2:file:/app/data/filedb
      - DB_USERNAME=sa
      - DB_PASSWORD='1qaz!QAZ'
      - SERVER_PORT=8080
      - MAX_FILE_SIZE=200MB
      - MAX_REQUEST_SIZE=200MB
      - APP_MAX_FILE_SIZE=200
      - DDL_AUTO=update
    restart: unless-stopped
```

启动服务:
```bash
docker-compose up -d
```

### 环境变量配置

| 变量名               | 默认值                           | 说明                                                |
|-------------------|-------------------------------|---------------------------------------------------|
| DB_URL            | jdbc:h2:file:/app/data/filedb | 数据库连接URL                                          |
| DB_USERNAME       | sa                            | 数据库用户名                                            |
| DB_PASSWORD       | 1qaz!QAZ                      | 数据库密码                                             |
| SERVER_PORT       | 8080                          | 服务端口                                              |
| MAX_FILE_SIZE     | 200MB                         | 最大文件大小                                            |
| MAX_REQUEST_SIZE  | 200MB                         | 最大请求大小                                            |
| APP_MAX_FILE_SIZE | 200 | 最大文件大小                                            |
| DDL_AUTO          | update | 控制数据库表结构的自动化管理。create：​​每次启动删除所有表并重新创建​​（清空历史数据！） |

### 自动化构建Docker镜像

#### 配置 Docker Hub 凭据

在 GitHub 仓库中设置以下 Secrets：

1. 进入 GitHub 仓库页面
2. 点击 `Settings` -> `Secrets and variables` -> `Actions`
3. 添加以下 Repository secrets：
   - `DOCKER_USERNAME`: 你的 Docker Hub 用户名
   - `DOCKER_PASSWORD`: 你的 Docker Hub 访问令牌（推荐）或密码

#### 修改 Docker 镜像名称

编辑 `.github/workflows/docker-build.yml` 文件，将以下行中的镜像名称替换为你的：

```yaml
env:
  DOCKER_IMAGE: your-dockerhub-username/file-sharing-app
```

改为：
```yaml
env:
  DOCKER_IMAGE: 你的用户名/你的镜像名
```

#### 获取 Docker Hub 访问令牌（推荐）

1. 登录 Docker Hub
2. 进入 `Account Settings` -> `Security`
3. 点击 `New Access Token`
4. 输入令牌名称，选择权限（建议选择 `Read, Write, Delete`）
5. 复制生成的令牌，用作 `DOCKER_PASSWORD`

#### 使用方法

##### 手动触发构建

1. 进入 GitHub 仓库页面
2. 点击 `Actions` 标签
3. 选择 `Build and Push Docker Image` 工作流
4. 点击 `Run workflow` 按钮
5. 配置参数：
   - **tag**: Docker 镜像标签（默认: latest）
   - **push_to_hub**: 是否推送到 Docker Hub（默认: true）
6. 点击 `Run workflow` 开始构建

##### 工作流功能

- ✅ 自动设置 Java 21 环境
- ✅ 使用 Gradle 构建项目
- ✅ 构建 Docker 镜像
- ✅ 推送到 Docker Hub
- ✅ 支持自定义标签
- ✅ 支持构建缓存优化
- ✅ 自动生成镜像元数据

##### 生成的镜像标签

工作流会自动生成以下标签：
- 用户指定的标签（默认 `latest`）
- 主分支会额外打上 `latest` 标签


## 配置说明

## 数据存储

- **数据库文件**: `./data/filedb.mv.db`
- **文件存储**: 文件以二进制形式存储在数据库中
- **文本存储**: 文本内容存储在数据库中

## 安全注意事项

1. **密码保护**: 建议为敏感文件和文本设置密码
2. **文件大小限制**: 默认限制为200MB，可根据需要调整
3. **数据库密码**: 生产环境请修改默认数据库密码
4. **HTTPS**: 生产环境建议使用HTTPS协议

## 故障排除

### 常见问题

1. **文件上传失败**
   - 检查文件大小是否超过限制
   - 检查磁盘空间是否充足

2. **数据库连接失败**
   - 检查数据库文件路径是否正确
   - 检查数据库文件权限

3. **端口占用**
   - 检查8080端口是否被其他应用占用
   - 修改配置文件中的端口设置

### 日志查看

```bash
# 查看应用日志
docker logs file-sharing

# 实时查看日志
docker logs -f file-sharing
```

## 开发贡献

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系:
- 提交 Issue
- 发送邮件
- 创建 Pull Request

---

**注意**: 本项目仅用于学习和开发目的，生产环境使用前请进行充分的安全评估和测试。