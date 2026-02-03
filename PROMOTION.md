# AutoJava CLI 推广文案模板

## 1. GitHub Release 描述（复制粘贴）

```markdown
## AutoJava CLI v1.0.1 - Java 代码审计工具

AutoJava 是一款基于纯 Java 开发的命令行工具，帮助开发者快速发现代码中的潜在问题。

### 🚀 快速开始

```bash
# 下载并解压后
java -jar autojav-cli-1.0.1.jar audit your-code.java
```

### ✨ 主要功能

- **代码审计**：基于 JavaParser 静态分析，识别 NPE、SQL 注入等常见问题
- **示例丰富**：内置多种漏洞示例，帮助学习安全编码
- **轻量快速**：纯 Java 实现，启动快，无依赖
- **中文友好**：错误提示和建议全中文

### 📦 包含内容

- `autojav-cli-1.0.1.jar` - 主程序
- `examples/` - 示例代码（SQL注入、空指针等）
- `test-all-examples.bat` - 批量测试脚本

### 📚 文档

- [使用说明](https://github.com/sunheyi6/autojav#使用说明)
- [示例文档](examples/README.md)

### 📝 更新日志

- 改进路径错误提示
- 添加丰富的示例文件
- 修复 SQL 注入检测规则
```

---

## 2. V2EX 推广帖

**标题**：[开源] 写了个 Java 代码审计工具，支持 SQL 注入/NPE 检测，附赠学习示例

**正文**：

```
最近写了个 Java 代码审计工具 AutoJava CLI，基于 JavaParser 做静态分析，可以检测常见的代码问题。

**能做什么**
- 检测 SQL 注入漏洞
- 检测空指针风险
- 检测资源未关闭
- 附带示例代码帮助学习

**特点**
- 纯 Java 实现，就一个 JAR 包
- 命令行工具，CI/CD 友好
- 中文提示，对国内开发者友好
- 示例丰富，适合学习代码安全

**下载**
GitHub Releases: https://github.com/sunheyi6/autojav/releases

**示例输出**
```
$ java -jar autojav-cli-1.0.1.jar audit UserService.java

审计结果（共 2 个问题）:
[WARNING] SQL注入检查: 潜在SQL注入风险 (line:29)
[WARNING] SQL注入检查: 潜在SQL注入风险 (line:43)
```

目前刚发布，欢迎大家试用提意见。
如果对你有帮助，给个 Star 支持下～

---
**开源地址**: https://github.com/sunheyi6/autojav
```

---

## 3. 掘金/知乎文章大纲

**标题**：我用 Java 写了一个代码审计工具，顺便聊聊常见的代码安全问题

**文章结构**：

1. **引言**（为什么写这个工具）
   - 平时 Code Review 容易遗漏问题
   - 想自动化检测常见漏洞
   - 顺便学习 JavaParser

2. **工具介绍**（截图展示）
   - 安装使用（简单）
   - 功能演示（动图/GIF）
   - 示例代码审计过程

3. **技术实现**（核心技术点）
   - JavaParser 解析 AST
   - 审计规则设计思路
   - 如何检测 SQL 注入
   - 如何检测空指针

4. **学习资源**（示例代码说明）
   - SQL 注入的各种写法
   - 空指针的常见场景
   - 如何写出安全的代码

5. **未来规划**
   - 支持更多规则
   - AI 辅助修复
   - IDE 插件

6. **结语**
   - 开源不易，求 Star
   - 欢迎 PR 和 Issue

---

## 4. 技术交流群话术

**发群里的文案**：

```
各位大佬，写了个 Java 代码审计的小工具，可以检测 SQL 注入、空指针这些常见问题。

🔧 命令行工具，就一个 JAR 包
📦 带示例代码，适合学习安全编码
⭐ 开源免费，GitHub 求 Star

下载: https://github.com/sunheyi6/autojav/releases

有 bug 或者建议欢迎提 issue～
```

---

## 5. 微信公众号/朋友圈

**短文案**：

```
推荐一个 Java 代码审计工具 AutoJava CLI

✅ 检测 SQL 注入、空指针
✅ 纯 Java，开箱即用
✅ 附赠学习示例

GitHub: sunheyi6/autojav
求 Star 支持～
```

**长文案**（带截图）：

```
【工具推荐】Java 开发者看过来 👀

写代码时总担心有安全漏洞？试试 AutoJava CLI

🎯 主要功能：
• SQL 注入检测
• 空指针检查
• 资源泄漏检查

💡 亮点：
• 纯 Java 实现，就一个 JAR
• 命令行工具，CI/CD 可用
• 附带示例，边用边学

适合：
✓ 学习代码安全的同学
✓ 需要做 Code Review 的组长
✓ 想自动化检查的团队

📎 开源地址：GitHub/sunheyi6/autojav

觉得有用的话，去点个 Star 支持下呗～ ⭐
```

---

## 6. 邮件/私信推广（给潜在客户）

```
主题：推荐一个 Java 代码审计工具 - AutoJava CLI

Hi，

我是 AutoJava CLI 的作者，开发了一款 Java 代码审计工具，想推荐给你试试。

**为什么推荐**：
1. 轻量级 - 就一个 JAR 包，不像 SonarQube 那么重
2. 中文友好 - 错误提示全中文，团队容易上手
3. 示例丰富 - 带 SQL 注入、空指针等示例，适合学习

**适合场景**：
- 个人开发者做代码自查
- 技术团队 Code Review 前预审
- 企业安全培训演示

**下载试用**：
https://github.com/sunheyi6/autojav/releases

如有任何建议，欢迎回复邮件或提 Issue。

Best,
[你的名字]
```

---

## 推广渠道清单

- [ ] V2EX（技术社区）
- [ ] 掘金（技术博客）
- [ ] 知乎（技术问答）
- [ ] CSDN（技术博客）
- [ ] 微信公众号（技术号投稿）
- [ ] B站（录制演示视频）
- [ ] 抖音/视频号（短视频介绍）
- [ ] 技术群（QQ群/微信群）
- [ ] 朋友圈（好友传播）
- [ ] 公司内部分享

