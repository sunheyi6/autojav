package com.autojav.core;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.util.List;

class PerformanceTest {

    public static void main(String[] args) throws IOException {
        CodeParser codeParser = new CodeParser();

        // 测试文件解析性能
        testFileParsing(codeParser);

        // 测试目录解析性能
        testDirectoryParsing(codeParser);
    }

    private static void testFileParsing(CodeParser codeParser) throws IOException {
        File testFile = new File("src/main/java/com/autojav/core/CodeParser.java");
        long startTime = System.currentTimeMillis();
        CompilationUnit cu = codeParser.parseFile(testFile);
        long endTime = System.currentTimeMillis();
        System.out.println("文件解析时间: " + (endTime - startTime) + "ms");
        System.out.println("提取的类数量: " + codeParser.extractClasses(cu).size());
        System.out.println("提取的方法数量: " + codeParser.extractMethods(cu).size());
    }

    private static void testDirectoryParsing(CodeParser codeParser) throws IOException {
        File testDir = new File("src/main/java/com/autojav/core");
        long startTime = System.currentTimeMillis();
        List<CompilationUnit> compilationUnits = codeParser.parseDirectory(testDir, true);
        long endTime = System.currentTimeMillis();
        System.out.println("目录解析时间: " + (endTime - startTime) + "ms");
        System.out.println("解析的文件数量: " + compilationUnits.size());
    }
}
