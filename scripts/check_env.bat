@echo off
echo.
echo ================================================
echo        AutoJava CLI 环境检查脚本
echo ================================================
echo.

echo 正在检查系统环境...
echo.

REM 检查Java
echo 1. 检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo    ❌ 未找到Java。请安装Java 17或更高版本。
    set JAVA_OK=false
) else (
    for /f "tokens=3 delims= " %%a in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VERSION=%%a
    set JAVA_VERSION=%JAVA_VERSION:"=%
    echo    ✅ Java版本: %JAVA_VERSION%
    
    REM 检查Java版本是否>=17
    for /f "tokens=1,2 delims=." %%a in ("%JAVA_VERSION%") do (
        set MAJOR=%%b
        if "%%a"=="1" (set MAJOR=%%b) else (set MAJOR=%%a)
    )
    if %MAJOR% geq 17 (
        echo    ✅ Java版本满足要求
        set JAVA_OK=true
    ) else (
        echo    ❌ Java版本过低，需要Java 17或更高版本
        set JAVA_OK=false
    )
)
echo.

REM 检查Maven
echo 2. 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo    ❌ 未找到Maven。请安装Maven 3.6.0或更高版本。
    set MAVEN_OK=false
) else (
    echo    ✅ Maven已安装
    set MAVEN_OK=true
)
echo.

REM 检查项目文件
echo 3. 检查项目文件...
if exist "pom.xml" (
    echo    ✅ pom.xml 存在
    set POM_OK=true
) else (
    echo    ❌ pom.xml 不存在
    set POM_OK=false
)

if exist "src" (
    echo    ✅ src 目录存在
    set SRC_OK=true
) else (
    echo    ⚠ src 目录不存在
    set SRC_OK=false
)
echo.

REM 检查构建目录
echo 4. 检查构建目录...
if exist "target" (
    echo    ✅ target 目录存在
    set TARGET_OK=true
) else (
    echo    ⚠ target 目录不存在（需要先构建项目）
    set TARGET_OK=false
)
echo.

REM 总结
echo ================================================
echo                    检查结果
echo ================================================
set ALL_OK=true

if "%JAVA_OK%"=="false" (
    echo ❌ Java环境: 不满足要求
    set ALL_OK=false
) else (
    echo ✅ Java环境: 满足要求
)

if "%MAVEN_OK%"=="false" (
    echo ❌ Maven环境: 不满足要求
    set ALL_OK=false
) else (
    echo ✅ Maven环境: 满足要求
)

if "%POM_OK%"=="false" (
    echo ❌ 项目文件: 不完整
    set ALL_OK=false
) else (
    echo ✅ 项目文件: 完整
)

if "%TARGET_OK%"=="false" (
    echo ⚠ 构建状态: 未构建
) else (
    echo ✅ 构建状态: 已构建
)

echo.
if "%ALL_OK%"=="true" (
    echo 🎉 所有必需条件均已满足！
    echo.
    echo 您可以：
    echo   1. 运行 start.bat 启动程序
    echo   2. 运行 build.bat 重新构建项目
) else (
    echo ⚠ 存在不满足的条件，请按提示解决问题后再试。
    echo.
    echo 建议：
    echo   - 确保Java 17+ 已正确安装
    echo   - 确保Maven已正确安装并加入PATH
    echo   - 确保项目文件完整
)
echo ================================================

echo.
pause