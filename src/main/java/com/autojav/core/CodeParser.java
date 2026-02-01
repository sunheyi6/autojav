package com.autojav.core;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CodeParser {

    private JavaParser javaParser;

    public CodeParser() {
        this.javaParser = new JavaParser();
    }

    /**
     * 解析单个Java文件
     * @param file Java文件
     * @return 编译单元
     * @throws IOException IO异常
     */
    public CompilationUnit parseFile(File file) throws IOException {
        if (!file.exists() || !file.isFile() || !file.getName().endsWith(".java")) {
            throw new IllegalArgumentException("无效的Java文件: " + file.getAbsolutePath());
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            ParseResult<CompilationUnit> result = javaParser.parse(fis);
            if (result.isSuccessful()) {
                log.debug("文件解析成功: {}", file.getAbsolutePath());
                return result.getResult().orElse(null);
            } else {
                log.error("文件解析失败: {}", file.getAbsolutePath());
                result.getProblems().forEach(problem -> log.error("解析问题: {}", problem.getMessage()));
                return null;
            }
        }
    }

    /**
     * 解析目录中的Java文件
     * @param directory 目录
     * @param recursive 是否递归
     * @return 编译单元列表
     * @throws IOException IO异常
     */
    public List<CompilationUnit> parseDirectory(File directory, boolean recursive) throws IOException {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("无效的目录: " + directory.getAbsolutePath());
        }

        List<CompilationUnit> compilationUnits = new ArrayList<>();
        parseDirectoryRecursive(directory, recursive, compilationUnits);
        return compilationUnits;
    }

    /**
     * 递归解析目录
     * @param directory 目录
     * @param recursive 是否递归
     * @param compilationUnits 编译单元列表
     * @throws IOException IO异常
     */
    private void parseDirectoryRecursive(File directory, boolean recursive, List<CompilationUnit> compilationUnits) throws IOException {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                // 过滤测试类
                if (!file.getName().endsWith("Test.java") && !file.getName().endsWith("Tests.java")) {
                    CompilationUnit cu = parseFile(file);
                    if (cu != null) {
                        compilationUnits.add(cu);
                    }
                }
            } else if (file.isDirectory() && recursive) {
                parseDirectoryRecursive(file, recursive, compilationUnits);
            }
        }
    }

    /**
     * 提取类信息
     * @param cu 编译单元
     * @return 类声明列表
     */
    public List<ClassOrInterfaceDeclaration> extractClasses(CompilationUnit cu) {
        List<ClassOrInterfaceDeclaration> classes = new ArrayList<>();
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, Void arg) {
                super.visit(n, arg);
                classes.add(n);
            }
        }, null);
        return classes;
    }

    /**
     * 提取方法信息
     * @param cu 编译单元
     * @return 方法声明列表
     */
    public List<MethodDeclaration> extractMethods(CompilationUnit cu) {
        List<MethodDeclaration> methods = new ArrayList<>();
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration n, Void arg) {
                super.visit(n, arg);
                methods.add(n);
            }
        }, null);
        return methods;
    }

    /**
     * 提取方法调用
     * @param cu 编译单元
     * @return 方法调用列表
     */
    public List<MethodCallExpr> extractMethodCalls(CompilationUnit cu) {
        List<MethodCallExpr> methodCalls = new ArrayList<>();
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodCallExpr n, Void arg) {
                super.visit(n, arg);
                methodCalls.add(n);
            }
        }, null);
        return methodCalls;
    }

    /**
     * 提取方法参数
     * @param method 方法声明
     * @return 参数列表
     */
    public List<Parameter> extractParameters(MethodDeclaration method) {
        return method.getParameters();
    }

    /**
     * 获取文件中的代码行数
     * @param file Java文件
     * @return 代码行数
     * @throws IOException IO异常
     */
    public int getLineCount(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int count = 0;
            int n;
            while ((n = fis.read(buffer)) > 0) {
                for (int i = 0; i < n; i++) {
                    if (buffer[i] == '\n') {
                        count++;
                    }
                }
            }
            return count;
        }
    }

    public static void main(String[] args) {
        CodeParser parser = new CodeParser();
        try {
            // 测试文件解析
            File testFile = new File("src/main/java/com/autojav/cli/Main.java");
            CompilationUnit cu = parser.parseFile(testFile);
            if (cu != null) {
                System.out.println("文件解析成功");
                System.out.println("类数量: " + parser.extractClasses(cu).size());
                System.out.println("方法数量: " + parser.extractMethods(cu).size());
            }
        } catch (IOException e) {
            log.error("测试解析失败", e);
        }
    }
}
