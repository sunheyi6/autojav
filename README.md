# AutoJava CLI - Java后端AI提效工具

## 项目简介

AutoJava是一款基于纯Java技术栈开发的命令行工具，旨在通过AI技术提升Java后端开发效率，解决重复劳动问题。

## 核心功能

### 1. 代码审计与自动修复
- 基于JavaParser解析Java源码，识别代码结构与潜在问题
- 集成阿里巴巴Java开发规范的静态审计规则
- 对接大模型API进行深度代码分析
- 支持AI生成修复方案，预览和一键替换源码
- 自动生成备份文件，确保代码安全

### 2. 自动接口文档生成
- 解析Spring系列注解，提取接口路径、请求方式、参数、返回体信息
- 支持生成Markdown格式文档
- 支持生成OpenAPI 3.0规范格式文档
- 支持文档导出和导入

### 3. 配置管理
- 支持本地和全局配置
- 支持大模型API密钥配置
- 支持配置的查看、设置和管理

### 4. 许可证管理
- 支持免费版、团队版、企业版、买断版四种版本
- 基于版本的功能权限控制
- 支持许可证激活和验证

## 配置管理

### 配置文件说明

**AutoJava CLI 使用以下配置文件：**

1. **.autojav.properties**
   - **作用**: 主要配置文件，存储实际的配置值
   - **位置**: 项目根目录
   - **内容**: 包含API密钥、模型设置、项目配置等
   - **注意**: 此文件已在版本控制中使用占位符，本地开发时会自动使用实际配置

2. **config-template.properties**
   - **作用**: 配置模板文件，提供所有配置项的参考
   - **位置**: 项目根目录
   - **内容**: 展示所有可用配置项及其说明
   - **使用方法**: 可复制为 `.autojav.properties` 并根据需要修改

3. **.autojav.properties.local** (可选)
   - **作用**: 本地配置文件，优先级高于主配置文件
   - **位置**: 项目根目录
   - **内容**: 存储本地开发环境的实际API密钥
   - **注意**: 此文件已添加到 `.gitignore`，不会被推送到版本控制系统

### 配置管理功能

- 支持本地和全局配置
- 支持大模型API密钥配置
- 支持配置的查看、设置和管理

## 技术栈

- **基础架构**: 纯Java 8+
- **CLI框架**: Picocli
- **代码解析**: JavaParser
- **AI调度**: 适配国内大模型（通义千问、文心一言、讯飞星火、Kimi等）
- **HTTP客户端**: OkHttp
- **日志**: SLF4J + Logback
- **构建**: Maven
- **测试**: JUnit 5

## 环境要求

- **Java版本**: Java 17 或更高版本
- **构建工具**: Maven 3.6.0+

## 安装与使用

### 安装

1. 克隆项目到本地
2. 进入项目目录
3. 执行Maven构建命令

```bash
mvn clean package
```

4. 构建完成后，在`target`目录下会生成可执行的FatJar文件

### 启动项目

#### 1. 构建项目

```bash
# 在项目根目录执行
mvn clean compile

# 或者打包完整jar文件
mvn clean package
```

#### 2. 运行项目

```bash
# 查看帮助信息
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar --help

# 或者直接运行主类
mvn exec:java -Dexec.mainClass="com.autojav.cli.Main"
```

#### 3. 配置AI服务（可选）

首次使用AI功能前，需要配置API密钥：

```bash
# 设置Kimi API密钥
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.api.key <your-kimi-api-key>

# 设置AI模型
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.model.name moonshot-v1

# 设置服务商类型
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.service.type kimi

# 查看所有配置
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config list
```

#### 4. 使用功能

##### 快速开始（使用示例文件）

如果你刚下载了工具，可以使用内置的示例文件快速体验：

```bash
# 解压 releases/autojav-cli-1.0.1.zip 后，进入解压目录
# 示例文件位于 examples/ 目录下

# 审计 SQL 注入示例文件
java -jar autojav-cli-1.0.1.jar audit examples/sql-injection/UserService.java
```

**注意**: `examples/sql-injection/UserService.java` 是示例占位符路径，请确保该文件存在于你的当前工作目录中，或替换为实际的 Java 文件路径。

##### 代码审计
```bash
# 审计单个Java文件（使用绝对路径或相对路径）
java -jar autojav-cli-1.0.1.jar audit /path/to/your/ActualFile.java

# 审计当前目录下的文件
java -jar autojav-cli-1.0.1.jar audit ./MyClass.java

# 审计整个目录
java -jar autojav-cli-1.0.1.jar audit /path/to/your/directory -r

# 使用AI进行深度审计（需要先配置API密钥）
java -jar autojav-cli-1.0.1.jar audit /path/to/your/File.java -ai

# 自动修复代码问题
java -jar autojav-cli-1.0.1.jar audit /path/to/your/File.java -f
```

##### 文档生成
```bash
# 生成Markdown格式文档
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar doc path/to/your/directory -f markdown

# 生成OpenAPI格式文档
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar doc path/to/your/directory -f openapi
```

##### 许可证管理
```bash
# 查看许可证信息
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar license info

# 激活许可证
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar license activate <your-license-key>
```

### 5. 配置管理

```bash
java -jar target/autojav-cli-1.0.0.jar config <operation> [key] [value]
```

**操作**:
- `set`: 设置配置
- `get`: 获取配置
- `list`: 列出所有配置

**AI配置项**:
- `ai.api.key`: AI服务API密钥（必需）
- `ai.model.name`: AI模型名称（如moonshot-v1用于Kimi）
- `ai.temperature`: AI响应的随机性（0.0-1.0，默认0.3）
- `ai.timeout.seconds`: API请求超时时间（秒）
- `ai.service.type`: AI服务提供商类型（如qwen, ernie, spark, kimi）

**示例**:

```bash
# 设置Kimi API密钥
java -jar autojav-cli-1.0.0.jar config set ai.api.key <your-kimi-api-key>

# 设置AI模型
java -jar autojav-cli-1.0.0.jar config set ai.model.name moonshot-v1

# 设置服务商类型
java -jar autojav-cli-1.0.0.jar config set ai.service.type kimi

# 获取AI API密钥
java -jar autojav-cli-1.0.0.jar config get ai.api.key

# 列出所有配置
java -jar autojav-cli-1.0.0.jar config list
```

## 支持的AI服务

本工具支持多种国内外大模型服务：

- **通义千问** (阿里云): 支持最新版本的Qwen模型
- **文心一言** (百度): 支持ERNIE Bot系列模型
- **讯飞星火** (科大讯飞): 支持Spark系列模型
- **Kimi** (月之暗面): 支持Moonshot系列模型
- **其他模型**: 可扩展支持更多大模型服务

## 版本说明

### 免费版
- 基础代码审计功能
- 基础文档生成功能
- 配置管理功能
- **限制**: 不支持代码修复、AI审计、自定义模板等高级功能

### 团队版
- 包含免费版所有功能
- 代码修复功能
- AI审计功能
- 团队协作功能

### 企业版
- 包含团队版所有功能
- 自定义模板功能
- 私有化部署支持
- CI/CD集成支持

### 买断版
- 包含企业版所有功能
- 永久使用权限

## 注意事项

1. 使用AI相关功能需要配置大模型API密钥
2. 代码修复功能会修改原始文件，请谨慎使用
3. 审计结果仅供参考，最终修复方案请由开发者确认
4. 本工具仅在本地处理代码，不会上传代码到云端，保障代码安全

## 贡献

欢迎提交Issue和Pull Request，共同改进AutoJava工具。

## 许可证

MIT License