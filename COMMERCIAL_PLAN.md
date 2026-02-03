# AutoJava CLI 商业化规划

## 目标用户
- Java 后端开发者
- 技术团队负责人
- 企业代码审查人员

## 产品分层

### 1. 免费版（Free）
**目标**: 吸引用户、建立信任、口碑传播

**包含功能**:
- 基础静态代码审计（5个规则）
  - 空指针检查
  - SQL注入检测
  - 资源未关闭检测
  - 异常处理检查
  - 硬编码密码检测
- 基础命令行工具
- 示例代码库

**分发方式**:
- GitHub Releases 免费下载
- 开源核心代码（建立信任）

---

### 2. 团队版（Team）¥99/人/年
**目标**: 个人开发者、小团队

**包含功能**:
- 免费版全部功能
- AI 深度代码审计（接入大模型）
- 自动代码修复建议
- 20+ 高级审计规则
  - 并发安全问题
  - 性能优化建议
  - 设计模式检查
  - API 规范检查
- 生成审计报告（PDF/HTML）

**分发方式**:
- 下载预编译 JAR（带许可证验证）
- 在线激活许可证

---

### 3. 企业版（Enterprise）¥999/人/年
**目标**: 中大型企业、金融机构

**包含功能**:
- 团队版全部功能
- 私有化部署（无需联网）
- 自定义审计规则
- CI/CD 集成（Jenkins/GitLab/GitHub Actions 插件）
- 团队协作功能
- 专属技术支持
- 源码交付（可选+¥5000）

**分发方式**:
- 企业私有部署包
- 定制化开发

---

## 技术架构分离

### 仓库规划

```
# 公开仓库 - autojav（建立社区信任）
autojav/
├── core/                    # 核心引擎（开源）
│   ├── parser/             # 代码解析
│   └── audit/base/         # 基础审计框架
├── cli/                    # 命令行工具（开源）
├── examples/               # 示例代码（开源）
└── README.md

# 私有仓库 - autojav-pro（付费功能）
autojav-pro/
├── ai/                     # AI集成模块
├── audit/rules/advanced/   # 高级规则库
├── fix/                    # 自动修复引擎
├── license/                # 许可证验证（加密）
└── ci-plugins/             # CI/CD插件

# 私有仓库 - autojav-enterprise（企业版）
autojav-enterprise/
├── server/                 # 私有化部署服务端
├── admin/                  # 管理后台
├── custom-rules/           # 自定义规则引擎
└── docs/                   # 企业文档
```

### 构建策略

```bash
# 开源版构建
mvn clean package -P free
# 输出: autojav-cli-free.jar

# 团队版构建
mvn clean package -P team
# 输出: autojav-cli-team.jar（包含加密的核心类）

# 企业版构建
mvn clean package -P enterprise
# 输出: autojav-cli-enterprise.jar + 服务端部署包
```

---

## 许可证系统

### 实现方式

```java
// LicenseManager.java（简化版）
public class LicenseManager {
    
    public enum VersionType {
        FREE,      // 免费版
        TRIAL,     // 试用版（7天）
        TEAM,      // 团队版
        ENTERPRISE // 企业版
    }
    
    public boolean checkFeature(String feature) {
        switch (versionType) {
            case FREE:
                return isFreeFeature(feature);
            case TEAM:
                return isTeamFeature(feature);
            case ENTERPRISE:
                return true; // 全部功能
        }
    }
}
```

### 激活流程

```
用户下载 JAR -> 运行 audit 命令 
  -> 检测到无许可证 -> 提示激活
  -> 输入许可证密钥 -> 在线验证
  -> 本地存储许可证 -> 解锁功能
```

---

## 营销推广策略

### 阶段一：种子用户（0-3个月）

1. **技术社区发布**
   - V2EX、掘金、CSDN、知乎
   - 标题：《开源 Java 代码审计工具，支持 AI 修复》

2. **开发者社群**
   - 技术群分享
   - GitHub 趋势榜
   - Gitee 推荐

3. **内容营销**
   - 写技术博客对比竞品
   - 录制使用视频
   - 发布漏洞案例分析

### 阶段二：付费转化（3-6个月）

1. **试用策略**
   - 默认开启 7 天团队版试用
   - 到期后降级到免费版
   - 试用期内展示付费功能价值

2. **定价策略**
   - 早鸟价：¥69/人/年（限时）
   - 正常价：¥99/人/年
   - 团队折扣：5人以上 8 折

3. **企业拓展**
   - 提供试用部署包
   - 线下技术交流
   - 定制化开发服务

---

## 竞品分析

| 产品 | 价格 | 优势 | 劣势 |
|------|------|------|------|
| SonarQube | ¥3000+/年 | 功能全面 | 太重、配置复杂 |
| Checkmarx | ¥10万+/年 | 企业级 | 极贵、只有企业版 |
| Alibaba P3C | 免费 | 阿里背书 | 规则固定、无AI |
| **AutoJava** | ¥99/年起 | 轻量、AI、中文友好 | 新品牌、需建信任 |

---

## 收入预测

| 阶段 | 时间 | 免费用户 | 付费用户 | 月收入 |
|------|------|---------|---------|--------|
| 起步 | 1-3月 | 100 | 5 | ¥500 |
| 成长 | 4-6月 | 1000 | 50 | ¥5000 |
| 稳定 | 7-12月 | 5000 | 200 | ¥20000 |
| 规模 | 第二年 | 20000 | 800 | ¥80000 |

---

## 下一步行动清单

### 本周必须完成
- [ ] 创建 GitHub Release v1.0.1
- [ ] 更新 README 添加下载链接
- [ ] 在 V2EX/掘金发布介绍文章
- [ ] 录制 3 分钟演示视频

### 本月计划
- [ ] 实现许可证验证系统
- [ ] 创建 autojav-examples 公开仓库
- [ ] 开通支付宝/微信支付
- [ ] 搭建简单官网（GitHub Pages）

### 三个月目标
- [ ] 1000+ GitHub Stars
- [ ] 100+ 付费用户
- [ ] 实现收支平衡

---

## 风险与应对

| 风险 | 应对策略 |
|------|---------|
| 代码被破解 | 核心验证逻辑混淆加密 |
| 竞品价格战 | 差异化定位（轻量+AI） |
| 用户不信任新品牌 | 开源核心建立信任 |
| AI 成本高 | 限制免费版调用次数 |

