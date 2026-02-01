# AutoJava CLI 使用说明

## 项目概述

AutoJava CLI 是一款专为Java后端开发者设计的AI提效工具，支持代码审计、文档生成、配置管理等功能。

## 启动方式

### 1. 一键启动（推荐）

双击运行 `start.bat` 文件，按照菜单提示进行操作。

### 2. 手动构建与运行

#### 构建项目
```bash
# 在项目根目录执行
mvn clean package
```

#### 运行工具
```bash
# 查看帮助
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar --help

# 查看版本
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar --version
```

## 主要功能

### 1. 代码审计 (`audit`)

```bash
# 审计单个Java文件
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit MyFile.java

# 审计整个目录（递归）
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit src/main/java -r

# 使用AI进行深度审计（需要配置API密钥）
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit MyFile.java -ai

# 自动修复代码问题
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit MyFile.java -f

# 预览修复结果（不实际修改文件）
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit MyFile.java -f -p
```

### 2. 文档生成 (`doc`)

```bash
# 生成Markdown格式文档
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar doc src/main/java -f markdown

# 生成OpenAPI格式文档
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar doc src/main/java -f openapi

# 生成文档并保存到指定文件
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar doc src/main/java -f markdown -o api-doc.md

# 递归处理子目录
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar doc src/main/java -r
```

### 3. 配置管理 (`config`)

```bash
# 设置配置项
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.api.key YOUR_API_KEY

# 获取配置项
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config get ai.api.key

# 查看所有配置
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config list

# 支持的配置项
# - ai.api.key: AI服务API密钥
# - ai.model.name: AI模型名称
# - ai.temperature: AI响应温度
# - ai.timeout.seconds: API请求超时时间
# - ai.service.type: AI服务提供商类型
```

### 4. 许可证管理 (`license`)

```bash
# 查看许可证信息
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar license info

# 激活许可证
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar license activate YOUR_LICENSE_KEY
```

## AI服务配置

### 支持的AI服务提供商

1. **通义千问 (Qwen)** - 阿里云的大模型服务
2. **文心一言 (Ernie)** - 百度的大模型服务
3. **讯飞星火 (Spark)** - 科大讯飞的大模型服务
4. **Kimi** - 月之暗面的大模型服务

### 配置示例

```bash
# 配置Kimi
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.api.key "your-kimi-api-key"
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.model.name "moonshot-v1"
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.service.type "kimi"

# 配置通义千问
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.api.key "your-qwen-api-key"
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.model.name "qwen-max"
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.service.type "qwen"

# 配置文心一言
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.api.key "your-ernie-api-key"
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.model.name "ernie-bot-4.5"
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.service.type "ernie"

# 配置讯飞星火
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.api.key "your-spark-api-key"
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.model.name "spark-v3.5"
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.service.type "spark"
```

## 常用命令组合

```bash
# 审计项目并使用AI分析
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit src/main/java -r -ai

# 生成项目文档
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar doc src/main/java -r -f markdown -o docs/api-reference.md

# 审计并自动修复
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit src/main/java -r -f
```

## 注意事项

1. **首次使用**：建议先配置AI服务密钥再使用AI相关功能
2. **代码安全**：工具仅在本地处理代码，不会上传到云端
3. **备份重要代码**：使用自动修复功能前请备份代码
4. **网络连接**：AI功能需要网络连接以调用大模型服务
5. **许可证**：部分高级功能需要有效许可证

## 故障排除

### 构建问题

- 确认Java版本为17或更高版本
- 确认Maven已正确安装
- 检查网络连接以下载依赖

### 运行问题

- 确认目标JAR文件存在于target目录
- 检查Java环境配置
- 查看错误日志获取更多信息

### AI功能问题

- 确认API密钥配置正确
- 检查网络是否可以访问对应的大模型服务
- 确认许可证支持AI功能