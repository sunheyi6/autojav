@echo off
chcp 65001 >nul
echo ==========================================
echo AutoJava CLI - 示例文件测试脚本
echo ==========================================
echo.

set JAR=autojav-cli-1.0.1.jar

if not exist %JAR% (
    echo 错误：找不到 %JAR%
    echo 请先构建项目
    exit /b 1
)

echo [1/4] 测试 SQL 注入基础示例...
java -jar %JAR% audit examples\sql-injection\UserService.java
echo.
echo.

echo [2/4] 测试 SQL 注入高级示例...
java -jar %JAR% audit examples\sql-injection\SqlInjectionAdvanced.java
echo.
echo.

echo [3/4] 测试空指针示例...
java -jar %JAR% audit examples\null-pointer\NullPointerExample.java
echo.
echo.

echo [4/4] 测试最佳实践示例（应该问题较少）...
java -jar %JAR% audit examples\best-practices\SecureCoding.java
echo.
echo.

echo ==========================================
echo 测试完成！
echo ==========================================
pause
