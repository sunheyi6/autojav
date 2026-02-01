# AutoJava CLI 启动指南

## 快速启动

### 1. 环境准备

在启动项目前，请确保已安装以下软件：

- **Java 17** 或更高版本
- **Maven 3.6.0** 或更高版本

检查环境：
```bash
java -version
mvn -version
```

### 2. 项目构建

首次运行前需要构建项目：

```bash
# 在项目根目录执行
mvn clean package
```

构建成功后，会在 `target` 目录下生成可执行的 JAR 文件。

### 3. 运行项目

#### 方式一：使用批处理脚本（推荐）

双击运行 `start.bat` 文件，然后按菜单提示操作。

#### 方式二：命令行运行

```bash
# 查看帮助
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar --help

# 运行主程序
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar
```

## 功能使用

### 代码审计功能

```bash
# 审计单个文件
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit path/to/your/file.java

# 审计目录（递归）
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit path/to/your/directory -r

# 使用AI进行深度审计（需要配置API密钥）
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit path/to/your/file.java -ai
```

### 文档生成功能

```bash
# 生成Markdown格式文档
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar doc path/to/your/directory -f markdown

# 生成OpenAPI格式文档
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar doc path/to/your/directory -f openapi
```

### 配置管理

```bash
# 设置API密钥（以通义千问为例）
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.api.key YOUR_API_KEY

# 设置模型名称
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.model.name qwen-max

# 查看所有配置
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config list
```

## 常见问题

### 1. 构建失败

如果构建失败，请检查：
- Java版本是否满足要求（Java 17+）
- Maven是否正确安装
- 网络连接是否正常（用于下载依赖）

### 2. 运行时错误

如果运行时出现错误，请检查：
- JAR文件是否存在于target目录
- Java环境是否正确配置

### 3. AI功能无法使用

使用AI功能前，请确保：
- 已正确配置API密钥
- 网络可以访问对应的大模型服务
- 许可证支持AI功能

## 项目结构

```
D:\project\autojav\
├── src/                 # 源代码目录
├── target/              # 构建输出目录
├── pom.xml             # Maven配置文件
├── README.md           # 项目说明
├── STARTUP_GUIDE.md    # 启动指南（本文档）
├── start.bat           # Windows启动脚本
└── .autojav.properties # 配置文件
```

## 注意事项

1. 首次使用建议先运行 `mvn clean package` 构建项目
2. 使用AI功能需要配置相应的大模型API密钥
3. 代码修复功能会修改原文件，建议先备份重要代码
4. 确保网络环境可以访问所需的大模型服务