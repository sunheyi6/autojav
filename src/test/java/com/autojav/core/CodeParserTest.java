package com.autojav.core;

import com.github.javaparser.ast.CompilationUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CodeParserTest {

    private CodeParser codeParser;

    @BeforeEach
    void setUp() {
        codeParser = new CodeParser();
    }

    @Test
    void testParseFile() throws IOException {
        // 使用当前测试文件作为测试对象
        File testFile = new File("src/test/java/com/autojav/core/CodeParserTest.java");
        CompilationUnit cu = codeParser.parseFile(testFile);
        assertNotNull(cu);
        assertEquals(1, codeParser.extractClasses(cu).size());
    }

    @Test
    void testParseDirectory() throws IOException {
        File testDir = new File("src/main/java/com/autojav/core");
        List<CompilationUnit> compilationUnits = codeParser.parseDirectory(testDir, false);
        assertFalse(compilationUnits.isEmpty());
    }

    @Test
    void testExtractMethods() throws IOException {
        File testFile = new File("src/test/java/com/autojav/core/CodeParserTest.java");
        CompilationUnit cu = codeParser.parseFile(testFile);
        List<com.github.javaparser.ast.body.MethodDeclaration> methods = codeParser.extractMethods(cu);
        assertFalse(methods.isEmpty());
    }

    @Test
    void testLineCount() throws IOException {
        File testFile = new File("src/test/java/com/autojav/core/CodeParserTest.java");
        int lineCount = codeParser.getLineCount(testFile);
        assertTrue(lineCount > 0);
    }
}
