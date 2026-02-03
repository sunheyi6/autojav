# AutoJava 使用示例

本目录包含 AutoJava 工具的使用示例，展示如何使用工具检测和修复常见的 Java 代码问题。

## 快速开始

### 下载 JAR 包后如何使用

**方式一：将 JAR 包和示例文件放在同一目录**

```bash
# 1. 下载 autojav-cli-1.0.0.jar
# 2. 将 JAR 包复制到 examples 目录
# 3. 进入 examples 目录
cd examples

# 审计单个文件
java -jar autojav-cli-1.0.0.jar audit sql-injection/UserService.java

# 批量审计所有示例
java -jar autojav-cli-1.0.0.jar audit . -r
```

**方式二：使用绝对路径（推荐）**

```bash
# JAR 包在任意目录，使用绝对路径审计示例文件
java -jar autojav-cli-1.0.0.jar audit D:\path\to\examples\sql-injection\UserService.java

# Windows 示例
java -jar autojav-cli-1.0.0.jar audit C:\Users\YourName\Downloads\examples\sql-injection\UserService.java

# Linux/Mac 示例
java -jar autojav-cli-1.0.0.jar audit /home/user/examples/sql-injection/UserService.java
```

**方式三：审计自己的项目**

```bash
# 审计你的项目目录
java -jar autojav-cli-1.0.0.jar audit D:\your\project\src -r

# 生成审计报告
java -jar autojav-cli-1.0.0.jar audit D:\your\project\src -r -o report.txt
```

---

## 示例目录

### 1. SQL 注入问题示例 (`sql-injection/`)

**文件**: [UserService.java](sql-injection/UserService.java)

**问题描述**:
- 直接拼接 SQL 语句，存在 SQL 注入风险
- 未使用 PreparedStatement 的参数化查询

**检测命令**:
```bash
java -jar autojav-cli-1.0.0.jar audit examples/sql-injection/UserService.java
```

**预期结果**:
- 检测到 SQL 注入风险
- 建议使用 PreparedStatement 参数化查询

**修复命令**:
```bash
java -jar autojav-cli-1.0.0.jar audit examples/sql-injection/UserService.java -ai -f
```

---

### 2. 空指针异常问题示例 (`null-pointer/`)

**文件**: [OrderService.java](null-pointer/OrderService.java)

**问题描述**:
- 未对可能为 null 的对象进行空值检查
- 直接调用 null 对象的方法会导致 NullPointerException

**检测命令**:
```bash
java -jar autojav-cli-1.0.0.jar audit examples/null-pointer/OrderService.java
```

**预期结果**:
- 检测到多处潜在的空指针异常风险
- 标记需要添加空值检查的位置

**修复命令**:
```bash
java -jar autojav-cli-1.0.0.jar audit examples/null-pointer/OrderService.java -ai -f
```

---

### 3. 资源未关闭问题示例 (`resource-leak/`)

**文件**: [FileProcessor.java](resource-leak/FileProcessor.java)

**问题描述**:
- 文件流、数据库连接等资源未正确关闭
- 可能导致资源泄漏和性能问题

**检测命令**:
```bash
java -jar autojav-cli-1.0.0.jar audit examples/resource-leak/FileProcessor.java
```

**预期结果**:
- 检测到资源未关闭的问题
- 建议使用 try-with-resources 语句

**修复命令**:
```bash
java -jar autojav-cli-1.0.0.jar audit examples/resource-leak/FileProcessor.java -ai -f
```

---

## 批量审计所有示例

```bash
# 审计整个 examples 目录
java -jar autojav-cli-1.0.0.jar audit examples/ -r

# 生成审计报告
java -jar autojav-cli-1.0.0.jar audit examples/ -r -o audit-report.txt
```

## 注意事项

1. **备份原文件**: 修复前工具会自动创建备份文件（.backup.时间戳.java）
2. **审查修复结果**: AI 修复后请仔细审查代码，确保修复正确
3. **测试验证**: 修复后建议运行测试用例验证功能正常
4. **版本控制**: 建议在 Git 仓库中操作，方便对比和回滚

## 更多信息

详细使用文档请访问: [docs.html](../website/docs.html)