# AutoJava CLI

轻量级 Java 代码审计工具，基于 JavaParser 静态分析。

## 功能特性

- **空指针检查** - 检测潜在的 NullPointerException 风险
- **SQL 注入检测** - 发现不安全的 SQL 拼接
- **文档生成** - 生成 Markdown/OpenAPI 格式文档
- **配置管理** - 灵活的本地配置管理

## 快速开始

### 下载

从 [GitHub Releases](https://github.com/sunheyi6/autojav/releases) 下载最新版本。

### 使用

```bash
# 审计单个文件
java -jar autojav-cli.jar audit YourFile.java

# 递归审计目录
java -jar autojav-cli.jar audit src/ -r

# 生成 Markdown 文档
java -jar autojav-cli.jar doc src/ -f markdown

# 生成 OpenAPI 文档
java -jar autojav-cli.jar doc src/ -f openapi

# 查看帮助
java -jar autojav-cli.jar --help
```

## 示例

```bash
# 审计示例代码
java -jar autojav-cli.jar audit examples/sql-injection/UserService.java
```

输出：
```
开始审计: examples/sql-injection/UserService.java
文件解析成功: UserService.java
审计结果（共 2 个问题）:
[WARNING] SQL注入检查: 潜在SQL注入风险...
```

## 专业版

需要更多高级功能？查看专业版：

- AI 深度代码审计
- 自动代码修复
- 更多审计规则
- 团队协作功能

👉 [AutoJava Pro](https://github.com/sunheyi6/autojav-pro)

## 系统要求

- Java 17 或更高版本

## 技术栈

- Java 17
- JavaParser - 代码解析
- Picocli - 命令行框架

## 开源协议

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！
