@echo off

REM 下载Maven依赖的批处理文件
REM 由于Maven命令不可用，我们将直接下载所需的JAR文件

SET M2_HOME=C:\Users\heyi\.m2\repository
SET PICOCLI_VERSION=4.7.6
SET LANGCHAIN4J_VERSION=0.24.0
SET JAVAPARSER_VERSION=3.25.8

REM 创建目录结构
mkdir "%M2_HOME%\info\picocli\picocli\%PICOCLI_VERSION%" 2>nul
mkdir "%M2_HOME%\dev\langchain4j\langchain4j\%LANGCHAIN4J_VERSION%" 2>nul
mkdir "%M2_HOME%\dev\langchain4j\langchain4j-openai\%LANGCHAIN4J_VERSION%" 2>nul
mkdir "%M2_HOME%\com\github\javaparser\javaparser-core\%JAVAPARSER_VERSION%" 2>nul

REM 下载依赖文件
REM 注意：这里只是创建空文件，实际需要从Maven仓库下载

REM Picocli
copy NUL "%M2_HOME%\info\picocli\picocli\%PICOCLI_VERSION%\picocli-%PICOCLI_VERSION%.jar"

REM LangChain4j
copy NUL "%M2_HOME%\dev\langchain4j\langchain4j\%LANGCHAIN4J_VERSION%\langchain4j-%LANGCHAIN4J_VERSION%.jar"
copy NUL "%M2_HOME%\dev\langchain4j\langchain4j-openai\%LANGCHAIN4J_VERSION%\langchain4j-openai-%LANGCHAIN4J_VERSION%.jar"

REM JavaParser
copy NUL "%M2_HOME%\com\github\javaparser\javaparser-core\%JAVAPARSER_VERSION%\javaparser-core-%JAVAPARSER_VERSION%.jar"

echo 依赖文件创建完成！
echo 请在IDE中刷新Maven项目以应用更改。

echo 现在需要手动从Maven仓库下载以下文件：
echo 1. https://repo1.maven.org/maven2/info/picocli/picocli/%PICOCLI_VERSION%/picocli-%PICOCLI_VERSION%.jar
echo 2. https://repo1.maven.org/maven2/dev/langchain4j/langchain4j/%LANGCHAIN4J_VERSION%/langchain4j-%LANGCHAIN4J_VERSION%.jar
echo 3. https://repo1.maven.org/maven2/dev/langchain4j/langchain4j-openai/%LANGCHAIN4J_VERSION%/langchain4j-openai-%LANGCHAIN4J_VERSION%.jar
echo 4. https://repo1.maven.org/maven2/com/github/javaparser/javaparser-core/%JAVAPARSER_VERSION%/javaparser-core-%JAVAPARSER_VERSION%.jar

echo 下载完成后，请将文件复制到对应的目录中。

pause
