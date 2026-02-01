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
public class OpenApiDocGenerator implements DocGenerator {

    @Override
    public String generate(List<CompilationUnit> compilationUnits) throws DocGenerationException {
        StringBuilder openapi = new StringBuilder();

        // OpenAPI 3.0 规范头部
        openapi.append("openapi: 3.0.0\n");
        openapi.append("info:\n");
        openapi.append("  title: 接口文档\n");
        openapi.append("  version: 1.0.0\n");
        openapi.append("  description: 自动生成的接口文档\n");
        openapi.append("paths:\n");

        for (CompilationUnit cu : compilationUnits) {
            // 提取类信息
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
            for (ClassOrInterfaceDeclaration clazz : classes) {
                // 检查是否是Controller类
                if (isController(clazz)) {
                    // 提取类级别的路径前缀
                    String classPath = extractClassPath(clazz);

                    // 提取方法信息
                    List<MethodDeclaration> methods = clazz.findAll(MethodDeclaration.class);
                    for (MethodDeclaration method : methods) {
                        // 检查是否是接口方法
                        if (isApiMethod(method)) {
                            // 提取接口路径和请求方式
                            String methodPath = extractPath(method);
                            String fullPath = classPath + methodPath;
                            String methodType = extractMethodType(method).toLowerCase();

                            // 构建OpenAPI路径条目
                            openapi.append("  '" + fullPath + "':\n");
                            openapi.append("    " + methodType + " :\n");
                            openapi.append("      summary: " + method.getNameAsString() + "\n");
                            openapi.append("      description: " + extractMethodDescription(method) + "\n");
                            openapi.append("      parameters:\n");

                            // 提取参数信息
                            List<Parameter> parameters = method.getParameters();
                            for (Parameter param : parameters) {
                                String paramName = param.getNameAsString();
                                String paramType = param.getTypeAsString();
                                openapi.append("        - name: " + paramName + "\n");
                                openapi.append("          in: query\n");
                                openapi.append("          required: false\n");
                                openapi.append("          schema:\n");
                                openapi.append("            type: " + mapJavaTypeToOpenApiType(paramType) + "\n");
                            }

                            openapi.append("      responses:\n");
                            openapi.append("        '200':\n");
                            openapi.append("          description: 成功\n");
                            openapi.append("          content:\n");
                            openapi.append("            application/json:\n");
                            openapi.append("              schema:\n");
                            openapi.append("                type: object\n");
                        }
                    }
                }
            }
        }

        return openapi.toString();
    }

    @Override
    public void generateAndSave(List<CompilationUnit> compilationUnits, File outputFile) throws DocGenerationException {
        try {
            String openapi;
            if (outputFile.getName().endsWith(".json")) {
                // 生成JSON格式的OpenAPI文档
                openapi = generateJson(compilationUnits);
            } else {
                // 默认生成YAML格式
                openapi = generate(compilationUnits);
            }
            Files.writeString(outputFile.toPath(), openapi);
            log.info("OpenAPI文档生成成功: {}", outputFile.getAbsolutePath());
        } catch (IOException e) {
            log.error("保存OpenAPI文档失败", e);
            throw new DocGenerationException("保存OpenAPI文档失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成JSON格式的OpenAPI文档
     * @param compilationUnits 编译单元列表
     * @return JSON格式的OpenAPI文档
     * @throws DocGenerationException 文档生成异常
     */
    private String generateJson(List<CompilationUnit> compilationUnits) throws DocGenerationException {
        StringBuilder openapi = new StringBuilder();

        // JSON格式的OpenAPI 3.0 规范
        openapi.append("{");
        openapi.append("\"openapi\": \"3.0.0\",");
        openapi.append("\"info\": {");
        openapi.append("\"title\": \"接口文档\",");
        openapi.append("\"version\": \"1.0.0\",");
        openapi.append("\"description\": \"自动生成的接口文档\"");
        openapi.append("},");
        openapi.append("\"paths\": {");

        boolean firstPath = true;
        for (CompilationUnit cu : compilationUnits) {
            // 提取类信息
            List<ClassOrInterfaceDeclaration> classes = cu.findAll(ClassOrInterfaceDeclaration.class);
            for (ClassOrInterfaceDeclaration clazz : classes) {
                // 检查是否是Controller类
                if (isController(clazz)) {
                    // 提取类级别的路径前缀
                    String classPath = extractClassPath(clazz);

                    // 提取方法信息
                    List<MethodDeclaration> methods = clazz.findAll(MethodDeclaration.class);
                    for (MethodDeclaration method : methods) {
                        // 检查是否是接口方法
                        if (isApiMethod(method)) {
                            // 提取接口路径和请求方式
                            String methodPath = extractPath(method);
                            String fullPath = classPath + methodPath;
                            String methodType = extractMethodType(method).toLowerCase();

                            if (!firstPath) {
                                openapi.append(",");
                            }
                            firstPath = false;

                            // 构建OpenAPI路径条目
                            openapi.append("\"" + fullPath + "\": {");
                            openapi.append("\"" + methodType + "\": {");
                            openapi.append("\"summary\": \"" + method.getNameAsString() + "\",");
                            openapi.append("\"description\": \"" + extractMethodDescription(method) + "\",");
                            openapi.append("\"parameters\": [");

                            // 提取参数信息
                            List<Parameter> parameters = method.getParameters();
                            for (int j = 0; j < parameters.size(); j++) {
                                Parameter param = parameters.get(j);
                                String paramName = param.getNameAsString();
                                String paramType = param.getTypeAsString();
                                if (j > 0) {
                                    openapi.append(",");
                                }
                                openapi.append("{");
                                openapi.append("\"name\": \"" + paramName + "\",");
                                openapi.append("\"in\": \"query\",");
                                openapi.append("\"required\": false,");
                                openapi.append("\"schema\": {");
                                openapi.append("\"type\": \"" + mapJavaTypeToOpenApiType(paramType) + "\"");
                                openapi.append("}");
                                openapi.append("}");
                            }

                            openapi.append("] ,");
                            openapi.append("\"responses\": {");
                            openapi.append("\"200\": {");
                            openapi.append("\"description\": \"成功\",");
                            openapi.append("\"content\": {");
                            openapi.append("\"application/json\": {");
                            openapi.append("\"schema\": {");
                            openapi.append("\"type\": \"object\"");
                            openapi.append("}");
                            openapi.append("}");
                            openapi.append("}");
                            openapi.append("}");
                            openapi.append("}");
                            openapi.append("}");
                            openapi.append("}");
                        }
                    }
                }
            }
        }

        openapi.append("}");
        openapi.append("}");

        return openapi.toString();
    }

    @Override
    public String getFormat() {
        return "openapi"; // 基于阿里巴巴Java规范：接口文档规范
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
     * 提取类级别的路径前缀
     * @param clazz 类声明
     * @return 路径前缀
     */
    private String extractClassPath(ClassOrInterfaceDeclaration clazz) {
        for (AnnotationExpr annotation : clazz.getAnnotations()) {
            if (annotation.getNameAsString().equals("RequestMapping")) {
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
        return "/";
    }

    /**
     * 提取接口路径
     * @param method 方法声明
     * @return 接口路径
     */
    private String extractPath(MethodDeclaration method) {
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
     * 提取方法描述
     * @param method 方法声明
     * @return 方法描述
     */
    private String extractMethodDescription(MethodDeclaration method) {
        // 简单实现，后续可以通过JavaDoc提取更详细的描述
        return "";
    }

    /**
     * 将Java类型映射到OpenAPI类型
     * @param javaType Java类型
     * @return OpenAPI类型
     */
    private String mapJavaTypeToOpenApiType(String javaType) {
        switch (javaType) {
            case "int":
            case "Integer":
                return "integer";
            case "long":
            case "Long":
                return "integer";
            case "float":
            case "Float":
            case "double":
            case "Double":
                return "number";
            case "boolean":
            case "Boolean":
                return "boolean";
            case "String":
                return "string";
            default:
                return "object";
        }
    }
}
