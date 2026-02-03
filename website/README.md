# AutoJava 官网

AutoJava 商业化官网页面，简约风格，强调功能实用性。

## 特点

- **单页面应用** - 所有内容在一个 HTML 文件中
- **响应式设计** - 完美适配桌面端和移动端
- **无需构建** - 直接使用，无需 npm 或打包工具
- **CDN 加速** - 使用 Tailwind CSS CDN 和 Google Fonts

## 页面结构

```
website/
├── index.html          # 官网主页（唯一文件）
└── README.md           # 本文件
```

## 包含板块

1. **导航栏** - 固定顶部，快速跳转
2. **Hero 区** - 核心价值主张 + CTA 按钮
3. **数据统计** - 展示产品优势数据
4. **功能介绍** - 6大核心功能卡片
5. **使用步骤** - 三步开始使用的简化流程
6. **演示代码** - CLI 命令行展示
7. **定价方案** - 4个版本对比（免费/团队/企业/买断）
8. **常见问题** - FAQ 解答用户疑虑
9. **底部 CTA** - 最终转化入口
10. **页脚** - 链接和版权信息

## 本地预览

直接在浏览器打开 `index.html` 即可：

```bash
# Windows
start index.html

# macOS
open index.html

# Linux
xdg-open index.html
```

或使用简单的 HTTP 服务器：

```bash
# Python 3
python -m http.server 8080

# Node.js
npx serve .
```

然后访问 http://localhost:8080

## 部署方式

### 1. GitHub Pages（推荐免费方案）

1. 将 `website` 目录推送到 GitHub 仓库
2. 进入仓库 Settings -> Pages
3. Source 选择 Deploy from a branch
4. Branch 选择 `main`，文件夹选择 `/website`
5. 保存后即可通过 `https://yourname.github.io/autojav` 访问

### 2. Vercel / Netlify（推荐）

1. 将代码推送到 GitHub
2. 在 Vercel/Netlify 导入项目
3. 设置根目录为 `website`
4. 自动部署，获得 HTTPS 域名

### 3. 自有服务器

将 `index.html` 上传到任意 Web 服务器的静态资源目录即可：

```bash
# Nginx 示例
/usr/share/nginx/html/autojav/
├── index.html

# Apache 示例
/var/www/html/autojav/
├── index.html
```

## 自定义修改

### 修改品牌信息

编辑 `index.html` 中的以下内容：

- 品牌名：搜索 `AutoJava` 进行替换
- 产品描述：修改 meta description 和 Hero 区段落
- GitHub 链接：将 `your-repo/autojav` 替换为实际仓库地址
- 下载链接：将 `releases/autojav-cli-1.0.0.jar` 替换为实际下载地址

### 修改定价

搜索 `"¥99"`、`"¥299"`、`"¥999"` 修改对应版本价格。

### 修改配色

在 `<script>` 标签中的 `tailwind.config` 内修改：

```javascript
colors: {
    primary: '#2563eb',      // 主色调（蓝色）
    'primary-dark': '#1d4ed8',
    secondary: '#0f172a',    // 次色调（深蓝黑）
}
```

## 注意事项

1. **CDN 依赖** - 页面依赖 Tailwind CSS CDN，如果网络不稳定可考虑下载到本地
2. **SEO** - 已配置基础 meta 标签，生产环境建议补充 Open Graph 标签
3. **分析** - 建议添加百度统计或 Google Analytics 代码跟踪访问数据

## 许可证

与主项目保持一致。
