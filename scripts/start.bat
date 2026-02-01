@echo off
echo.
echo ================================================
echo        AutoJava CLI - Java后端AI提效工具
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

REM 检查target目录和jar文件
if not exist "target" (
    echo 目标目录不存在，正在构建项目...
    call mvn clean package -DskipTests
    if %errorlevel% neq 0 (
        echo 构建失败，请检查错误信息。
        pause
        exit /b 1
    )
    echo 项目构建完成。
    echo.
)

REM 查找生成的jar文件
set JAR_FILE=
for /f "delims=" %%i in ('dir /b target\autojav-cli-*.jar 2^>nul') do set JAR_FILE=target\%%i

if "%JAR_FILE%"=="" (
    echo 未找到可执行的JAR文件，请先运行 mvn clean package 构建项目。
    pause
    exit /b 1
)

echo 可执行文件: %JAR_FILE%
echo.

:MENU
echo 请选择要执行的操作:
echo.
echo 1. 查看帮助信息
echo 2. 代码审计
echo 3. 文档生成
echo 4. 配置管理
echo 5. 许可证管理
echo 6. 重新构建项目
echo 0. 退出
echo.
choice /C 1234560 /M "请输入选项编号"

if errorlevel 7 goto :EXIT
if errorlevel 6 goto :REBUILD
if errorlevel 5 goto :LICENSE_MENU
if errorlevel 4 goto :CONFIG_MENU
if errorlevel 3 goto :DOC_MENU
if errorlevel 2 goto :AUDIT_MENU
if errorlevel 1 goto :HELP

:HELP
echo.
echo 运行: java -jar %JAR_FILE%
echo.
goto MENU

:AUDIT_MENU
echo.
set /p AUDIT_PATH=请输入要审计的Java文件或目录路径: 
if not "%AUDIT_PATH%"=="" (
    java -jar %JAR_FILE% audit "%AUDIT_PATH%"
)
echo.
pause
goto MENU

:DOC_MENU
echo.
set /p DOC_PATH=请输入要生成文档的Java文件或目录路径: 
if not "%DOC_PATH%"=="" (
    java -jar %JAR_FILE% doc "%DOC_PATH%" -f markdown
)
echo.
pause
goto MENU

:CONFIG_MENU
echo.
echo 配置管理菜单:
echo 1. 查看所有配置
echo 2. 设置API密钥
echo 3. 设置模型名称
choice /C 123 /M "请选择操作"

if errorlevel 3 (
    set /p MODEL_NAME=请输入模型名称 (如: qwen-max): 
    if not "%MODEL_NAME%"=="" (
        java -jar %JAR_FILE% config set ai.model.name "%MODEL_NAME%"
    )
)
if errorlevel 2 (
    set /p API_KEY=请输入API密钥: 
    if not "%API_KEY%"=="" (
        java -jar %JAR_FILE% config set ai.api.key "%API_KEY%"
    )
)
if errorlevel 1 (
    java -jar %JAR_FILE% config list
)
echo.
pause
goto MENU

:LICENSE_MENU
echo.
echo 许可证管理菜单:
echo 1. 查看许可证信息
echo 2. 激活许可证
choice /C 12 /M "请选择操作"

if errorlevel 2 (
    set /p LICENSE_KEY=请输入许可证密钥: 
    if not "%LICENSE_KEY%"=="" (
        java -jar %JAR_FILE% license activate "%LICENSE_KEY%"
    )
)
if errorlevel 1 (
    java -jar %JAR_FILE% license info
)
echo.
pause
goto MENU

:REBUILD
echo.
echo 正在重新构建项目...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo 构建失败
) else (
    echo 构建成功
)
echo.
pause
goto MENU

:EXIT
echo.
echo 感谢使用AutoJava CLI工具！
pause