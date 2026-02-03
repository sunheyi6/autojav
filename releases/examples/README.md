# AutoJava CLI 示例文件

本目录包含用于演示代码审计功能的示例文件。

## 目录结构

```
examples/
├── sql-injection/           # SQL注入漏洞示例
│   ├── UserService.java     # 基础SQL注入示例
│   └── SqlInjectionAdvanced.java  # 高级SQL注入场景
├── null-pointer/            # 空指针异常示例
│   └── NullPointerExample.java    # 各种NPE风险场景
└── best-practices/          # 安全编码最佳实践
    └── SecureCoding.java    # 展示如何编写安全代码
```

## 使用示例

### 1. SQL注入基础示例

```bash
java -jar autojav-cli-1.0.1.jar audit examples/sql-injection/UserService.java
```

**预期输出**：
- 发现 2 个 SQL 注入问题
- 分别在 `executeQuery(sql)` 调用处（第29行和第43行）

### 2. SQL注入高级示例

```bash
java -jar autojav-cli-1.0.1.jar audit examples/sql-injection/SqlInjectionAdvanced.java
```

**预期输出**：
- 发现 5 个 SQL 注入问题
- 覆盖多种注入场景：字符串拼接、String.format、StringBuilder、ORDER BY、LIKE子句等

### 3. 空指针示例

```bash
java -jar autojav-cli-1.0.1.jar audit examples/null-pointer/NullPointerExample.java
```

**预期输出**：
- 发现 2 个空指针风险
- 方法参数直接调用（input.length）
- 链式调用风险（other.items.size）

### 4. 最佳实践示例

```bash
java -jar autojav-cli-1.0.1.jar audit examples/best-practices/SecureCoding.java
```

**预期输出**：
- 发现 0-1 个问题（越少越好，展示安全代码写法）
- 展示正确使用 PreparedStatement、Objects.requireNonNull、Optional等

## 批量测试

运行 `test-all-examples.bat` 脚本测试所有示例：

```bash
cd releases
test-all-examples.bat
```

## 添加新的示例

如果要添加新的示例文件：

1. 在对应目录下创建 `.java` 文件
2. 在文件中添加清晰的注释说明预期检测的问题
3. 运行测试验证输出是否符合预期
4. 更新本 README 文档

## 注意事项

- 示例文件包含故意编写的漏洞代码，仅用于测试和学习
- 请勿在生产环境中使用示例中的危险代码
- 最佳实践示例展示了正确的安全编码方式
