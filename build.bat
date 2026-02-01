@echo off
echo.
echo ================================================
echo        AutoJava CLI 项目构建脚本
echo ================================================
echo.

REM 检查Java版本
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Java。请确保已安装Java 17或更高版本。
    pause
    exit /b 1
)

REM 获取Java版本信息
for /f "tokens=3 delims= " %%a in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VERSION=%%a
set JAVA_VERSION=%JAVA_VERSION:"=%

echo 检测到Java版本: %JAVA_VERSION%
echo.

REM 检查Maven
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo 错误: 未找到Maven。请确保Maven已正确安装并添加到PATH。
    pause
    exit /b 1
)

echo 正在清理并构建项目...
echo.

REM 执行Maven构建
call mvn clean package -DskipTests

if %errorlevel% equ 0 (
    echo.
    echo ================================================
    echo 项目构建成功！
    echo.
    echo 可执行JAR文件位于 target/ 目录下
    echo.
    echo 推荐下一步: 双击运行 start.bat 来使用程序
    echo ================================================
) else (
    echo.
    echo ================================================
    echo 项目构建失败，请检查错误信息
    echo ================================================
)

echo.
pause