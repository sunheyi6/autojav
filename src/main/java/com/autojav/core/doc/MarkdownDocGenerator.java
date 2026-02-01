package com.autojav.core.doc;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Slf4j
public class MarkdownDocGenerator implements DocGenerator {

    @Override
    public String generate(List<CompilationUnit> compilationUnits) throws DocGenerationException {
        StringBuilder markdown = new StringBuilder();

        // 文档标题
        markdown.append("# 接口文档\n\n");
        markdown.append("生成时间: " + java.time.LocalDateTime.now() + "\n\n");

        for (CompilationUnit cu : compilationUnits) {
            // 提取类信息
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
            for (ClassOrInterfaceDeclaration clazz : classes) {
                // 检查是否是Controller类
                if (isController(clazz)) {
                    markdown.append("## " + clazz.getNameAsString() + "\n\n");

                    // 提取方法信息
                    List<MethodDeclaration> methods = clazz.findAll(MethodDeclaration.class);
                    for (MethodDeclaration method : methods) {
                        // 检查是否是接口方法
                        if (isApiMethod(method)) {
                            markdown.append("### " + method.getNameAsString() + "\n\n");

                            // 提取接口路径和请求方式
                            String path = extractPath(method);
                            String methodType = extractMethodType(method);
                            markdown.append("**路径**: `" + path + "`\n\n");
                            markdown.append("**方法**: `" + methodType + "`\n\n");

                            // 提取参数信息
                            List<Parameter> parameters = method.getParameters();
                            if (!parameters.isEmpty()) {
                                markdown.append("**参数**:\n\n");
                                markdown.append("| 名称 | 类型 | 描述 |\n");
                                markdown.append("|------|------|------|\n");
                                for (Parameter param : parameters) {
                                    String paramName = param.getNameAsString();
                                    String paramType = param.getTypeAsString();
                                    String paramDesc = extractParamDescription(param);
                                    markdown.append("| " + paramName + " | " + paramType + " | " + paramDesc + " |\n");
                                }
                                markdown.append("\n");
                            }

                            // 提取返回类型
                            String returnType = method.getTypeAsString();
                            markdown.append("**返回类型**: `" + returnType + "`\n\n");

                            // 提取方法描述
                            String methodDesc = extractMethodDescription(method);
                            if (!methodDesc.isEmpty()) {
                                markdown.append("**描述**: " + methodDesc + "\n\n");
                            }

                            markdown.append("---\n\n");
                        }
                    }
                }
            }
        }

        return markdown.toString();
    }

    @Override
    public void generateAndSave(List<CompilationUnit> compilationUnits, File outputFile) throws DocGenerationException {
        try {
            String markdown = generate(compilationUnits);
            Files.writeString(outputFile.toPath(), markdown);
            log.info("Markdown文档生成成功: {}", outputFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("保存Markdown文档失败", e);
            throw new DocGenerationException("保存Markdown文档失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String getFormat() {
        return "markdown";
    }

    /**
     * 检查是否是Controller类
     * @param clazz 类声明
     * @return 是否是Controller类
     */
    private boolean isController(ClassOrInterfaceDeclaration clazz) {
        return clazz.getAnnotations().stream()
                .anyMatch(annotation -> annotation.getNameAsString().equals("RestController") || 
                        annotation.getNameAsString().equals("Controller"));
    }

    /**
     * 检查是否是API方法
     * @param method 方法声明
     * @return 是否是API方法
     */
    private boolean isApiMethod(MethodDeclaration method) {
        return method.getAnnotations().stream()
                .anyMatch(annotation -> annotation.getNameAsString().equals("GetMapping") ||
                        annotation.getNameAsString().equals("PostMapping") ||
                        annotation.getNameAsString().equals("PutMapping") ||
                        annotation.getNameAsString().equals("DeleteMapping") ||
                        annotation.getNameAsString().equals("PatchMapping"));
    }

    /**
     * 提取接口路径
     * @param method 方法声明
     * @return 接口路径
     */
    private String extractPath(MethodDeclaration method) {
        // 检查方法上的路径注解
        for (AnnotationExpr annotation : method.getAnnotations()) {
            String annotationName = annotation.getNameAsString();
            if (annotationName.endsWith("Mapping")) {
                if (annotation instanceof SingleMemberAnnotationExpr) {
                    SingleMemberAnnotationExpr singleMemberAnnotation = (SingleMemberAnnotationExpr) annotation;
                    return singleMemberAnnotation.getMemberValue().toString().replace("\"", "");
                } else if (annotation instanceof NormalAnnotationExpr) {
                    NormalAnnotationExpr normalAnnotation = (NormalAnnotationExpr) annotation;
                    for (MemberValuePair pair : normalAnnotation.getPairs()) {
                        if (pair.getNameAsString().equals("value")) {
                            return pair.getValue().toString().replace("\"", "");
                        }
                    }
                }
            }
        }
        return "/" + method.getNameAsString();
    }

    /**
     * 提取请求方式
     * @param method 方法声明
     * @return 请求方式
     */
    private String extractMethodType(MethodDeclaration method) {
        for (AnnotationExpr annotation : method.getAnnotations()) {
            String annotationName = annotation.getNameAsString();
            switch (annotationName) {
                case "GetMapping":
                    return "GET";
                case "PostMapping":
                    return "POST";
                case "PutMapping":
                    return "PUT";
                case "DeleteMapping":
                    return "DELETE";
                case "PatchMapping":
                    return "PATCH";
            }
        }
        return "GET"; // 默认GET
    }

    /**
     * 提取参数描述
     * @param parameter 参数
     * @return 参数描述
     */
    private String extractParamDescription(Parameter parameter) {
        // 简单实现，后续可以通过注解提取更详细的描述
        return "";
    }

    /**
     * 提取方法描述
     * @param method 方法声明
     * @return 方法描述
     */
    private String extractMethodDescription(MethodDeclaration method) {
        // 简单实现，后续可以通过JavaDoc提取更详细的描述
        return "";
    }
}
