# AutoJava CLI 快速入门

## 项目简介

AutoJava CLI 是一款基于Java开发的命令行工具，利用AI技术提升Java后端开发效率，支持代码审计、文档生成等功能。

## 快速启动

### 方法一：一键启动（Windows用户）

1. 双击运行 `start.bat`
2. 按照菜单提示操作

### 方法二：命令行启动

1. **构建项目**
   ```bash
   # 在项目根目录执行
   mvn clean package
   ```

2. **运行工具**
   ```bash
   java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar --help
   ```

## 主要功能演示

### 1. 代码审计
```bash
# 审计单个文件
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit MyFile.java

# 递归审计目录
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit src/main/java -r
```

### 2. 文档生成
```bash
# 生成Markdown文档
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar doc src/main/java -f markdown
```

### 3. AI功能使用
```bash
# 首先配置API密钥
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar config set ai.api.key YOUR_KEY

# 使用AI审计
java -jar target/autojav-cli-1.0.0-SNAPSHOT.jar audit MyFile.java -ai
```

## 配置说明

配置文件位于项目根目录的 `.autojav.properties`，支持以下配置：

- `ai.api.key`: AI服务API密钥
- `ai.model.name`: AI模型名称
- `ai.service.type`: AI服务提供商（qwen/ernie/spark）

## 环境要求

- Java 17+
- Maven 3.6+

## 文件说明

- `start.bat`: Windows启动脚本
- `build.bat`: 构建脚本
- `README.md`: 详细说明
- `USAGE.md`: 使用说明
- `STARTUP_GUIDE.md`: 启动指南
- `.autojav.properties`: 配置文件（需要手动创建）

## 下一步

1. 运行 `build.bat` 构建项目
2. 双击 `start.bat` 使用工具
3. 查看 `USAGE.md` 了解更多功能详情