@echo off
chcp 65001 >nul
echo ==========================================
echo AutoJava CLI - 创建 GitHub Release 脚本
echo ==========================================
echo.

:: 检查参数
if "%~1"=="" (
    echo 用法: create-release.bat [版本号]
    echo 示例: create-release.bat v1.0.1
    exit /b 1
)

set VERSION=%~1
echo 准备发布版本: %VERSION%
echo.

:: 检查必要文件
echo [1/5] 检查发布文件...
if not exist "releases\autojav-cli-%VERSION%.zip" (
    echo 错误: 找不到 releases\autojav-cli-%VERSION%.zip
    echo 请先构建项目: mvn clean package
    exit /b 1
)
echo ✓ 找到发布包
echo.

:: 检查 Git 状态
echo [2/5] 检查 Git 状态...
cd /d "%~dp0\.."
git status --short >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 当前目录不是 Git 仓库
    exit /b 1
)
echo ✓ Git 状态正常
echo.

:: 创建 Tag
echo [3/5] 创建 Git Tag...
git tag -a %VERSION% -m "Release %VERSION%"
if %errorlevel% neq 0 (
    echo 警告: Tag 可能已存在，尝试强制更新...
    git tag -d %VERSION% 2>nul
    git tag -a %VERSION% -m "Release %VERSION%"
)
echo ✓ 创建 Tag: %VERSION%
echo.

:: 推送 Tag
echo [4/5] 推送到远程仓库...
git push origin %VERSION%
if %errorlevel% neq 0 (
    echo 错误: 推送失败
    exit /b 1
)
echo ✓ 推送成功
echo.

:: 提示下一步
echo [5/5] 完成！下一步操作：
echo.
echo 1. 访问 GitHub Release 页面：
echo    https://github.com/sunheyi6/autojav/releases/new
echo.
echo 2. 点击 "Choose a tag"，选择: %VERSION%
echo.
echo 3. 填写发布信息：
echo    - Release title: AutoJava CLI %VERSION%
echo    - 复制下面的内容到描述框：
echo.
echo ---------- 复制以下内容 ----------
echo ## AutoJava CLI %VERSION%
echo.
echo ### 下载
echo - [autojav-cli-%VERSION%.zip](https://github.com/sunheyi6/autojav/releases/download/%VERSION%/autojav-cli-%VERSION%.zip)
echo.
echo ### 快速开始
echo ```bash
echo # 解压后运行
echo java -jar autojav-cli-%VERSION%.jar --help
echo.
echo # 审计示例代码
echo java -jar autojav-cli-%VERSION%.jar audit examples/sql-injection/UserService.java
echo ```
echo.
echo ### 功能特性
echo - 代码审计与自动修复
echo - SQL 注入检测
echo - 空指针检查
echo - 更多...
echo.
echo ---------- 复制结束 ----------
echo.
echo 4. 上传文件：releases\autojav-cli-%VERSION%.zip
echo.
echo 5. 点击 "Publish release"
echo.
echo ==========================================
echo 发布流程已完成！
echo ==========================================

pause
