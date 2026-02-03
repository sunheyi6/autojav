package com.autojav.core.audit.rules;

import com.autojav.core.audit.AuditResult;
import com.autojav.core.audit.AuditRule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NullPointerExceptionRule implements AuditRule {

    @Override
    public String getName() {
        return "空指针检查";
    }

    @Override
    public String getDescription() {
        return "检查可能导致空指针异常的代码，如直接调用可能为null的对象方法或访问其属性";
    }

    @Override
    public List<AuditResult> audit(CompilationUnit compilationUnit) {
        List<AuditResult> results = new ArrayList<>();
        String filePath = compilationUnit.getStorage().map(s -> s.getPath().toString()).orElse("unknown");

        // 收集方法参数（这些是高风险变量）
        Set<String> methodParams = new HashSet<>();
        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration method, Void arg) {
                super.visit(method, arg);
                for (Parameter param : method.getParameters()) {
                    // 只关注对象类型参数（非基本类型）
                    String type = param.getType().asString();
                    if (!isPrimitiveType(type)) {
                        methodParams.add(param.getNameAsString());
                    }
                }
            }
        }, null);

        // 检查方法调用中的NPE风险
        compilationUnit.accept(new VoidVisitorAdapter<Set<String>>() {
            @Override
            public void visit(MethodCallExpr methodCallExpr, Set<String> checkedVars) {
                super.visit(methodCallExpr, checkedVars);

                if (methodCallExpr.getScope().isPresent()) {
                    String scopeStr = methodCallExpr.getScope().get().toString();
                    
                    // 跳过常见的安全调用（如静态方法、this、super等）
                    if (isSafeScope(scopeStr)) {
                        return;
                    }

                    // 检查是否已经对该变量进行了空检查
                    if (checkedVars != null && checkedVars.contains(scopeStr)) {
                        return;
                    }

                    // 检查作用域是否为变量（NameExpr）或字段访问（FieldAccessExpr）
                    boolean isRisky = false;
                    String variableName = scopeStr;

                    if (methodCallExpr.getScope().get() instanceof NameExpr) {
                        // 如：obj.method()
                        NameExpr nameExpr = (NameExpr) methodCallExpr.getScope().get();
                        String varName = nameExpr.getNameAsString();
                        
                        // 如果是方法参数，标记为高风险
                        if (methodParams.contains(varName)) {
                            isRisky = true;
                        }
                        // 如果是方法参数且是成员变量调用，需要检查
                        // 但跳过常见的已初始化成员变量（如数据库连接）
                        if (isMemberVariable(compilationUnit, varName) && methodParams.contains(varName)) {
                            isRisky = true;
                        }
                    } else if (methodCallExpr.getScope().get() instanceof FieldAccessExpr) {
                        // 如：obj.field.method()
                        FieldAccessExpr fieldAccess = (FieldAccessExpr) methodCallExpr.getScope().get();
                        String fieldOwner = fieldAccess.getScope().toString();
                        
                        // 如果字段的所有者是方法参数，标记为高风险
                        if (methodParams.contains(fieldOwner) || isMemberVariable(compilationUnit, fieldOwner)) {
                            isRisky = true;
                            variableName = fieldOwner + "." + fieldAccess.getNameAsString();
                        }
                    } else if (methodCallExpr.getScope().get() instanceof MethodCallExpr) {
                        // 链式调用：method1().method2()
                        // 这种通常有风险，因为method1可能返回null
                        MethodCallExpr chainCall = (MethodCallExpr) methodCallExpr.getScope().get();
                        if (chainCall.getScope().isPresent()) {
                            String chainScope = chainCall.getScope().get().toString();
                            if (methodParams.contains(chainScope) || isMemberVariable(compilationUnit, chainScope)) {
                                isRisky = true;
                                variableName = chainScope;
                            }
                        }
                    }

                    if (isRisky) {
                        int line = methodCallExpr.getBegin().map(b -> b.line).orElse(0);
                        int column = methodCallExpr.getBegin().map(b -> b.column).orElse(0);
                        
                        // 检查是否有对该变量的null检查
                        boolean hasNullCheck = hasNullCheck(variableName, scopeStr, compilationUnit, methodCallExpr);
                        // 检查是否有Objects.requireNonNull校验
                        boolean hasRequireNonNull = hasRequireNonNullCheck(variableName, compilationUnit, methodCallExpr);
                        
                        if (!hasNullCheck && !hasRequireNonNull) {
                            results.add(new AuditResult(
                                    getName(),
                                    Severity.WARNING,
                                    "可能存在空指针异常风险：直接调用" + scopeStr + "的方法",
                                    filePath,
                                    line,
                                    column,
                                    "建议在调用方法前添加空指针检查：if (" + variableName + " != null)"
                            ));
                        }
                    }
                }
            }
        }, new HashSet<>());

        return results;
    }

    @Override
    public Severity getSeverity() {
        return Severity.WARNING;
    }

    /**
     * 检查是否为基本类型
     */
    private boolean isPrimitiveType(String type) {
        return type.equals("int") || type.equals("long") || type.equals("short") || 
               type.equals("byte") || type.equals("float") || type.equals("double") ||
               type.equals("boolean") || type.equals("char");
    }

    /**
     * 检查作用域是否为安全的（不需要NPE检查）
     */
    private boolean isSafeScope(String scope) {
        // 跳过this, super
        if (scope.equals("this") || scope.equals("super")) {
            return true;
        }
        // 跳过大写的静态常量
        if (scope.toUpperCase().equals(scope) && scope.length() > 1) {
            return true;
        }
        // 跳过已知的类名（大写开头）
        if (Character.isUpperCase(scope.charAt(0))) {
            return true;
        }
        return false;
    }

    /**
     * 检查是否为成员变量
     */
    private boolean isMemberVariable(CompilationUnit cu, String varName) {
        final boolean[] isMember = {false};
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(com.github.javaparser.ast.body.FieldDeclaration field, Void arg) {
                super.visit(field, arg);
                field.getVariables().forEach(v -> {
                    if (v.getNameAsString().equals(varName)) {
                        isMember[0] = true;
                    }
                });
            }
        }, null);
        return isMember[0];
    }

    /**
     * 检查是否有Objects.requireNonNull校验
     */
    private boolean hasRequireNonNullCheck(String varName, CompilationUnit compilationUnit, MethodCallExpr currentExpr) {
        final boolean[] hasCheck = {false};
        
        // 获取当前方法
        final com.github.javaparser.ast.body.MethodDeclaration[] currentMethod = {null};
        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration method, Void arg) {
                super.visit(method, arg);
                if (method.getBegin().isPresent() && currentExpr.getBegin().isPresent()) {
                    int methodStart = method.getBegin().get().line;
                    int methodEnd = method.getEnd().get().line;
                    int exprLine = currentExpr.getBegin().get().line;
                    if (exprLine >= methodStart && exprLine <= methodEnd) {
                        currentMethod[0] = method;
                    }
                }
            }
        }, null);

        if (currentMethod[0] == null) {
            return false;
        }

        // 检查是否有Objects.requireNonNull(var, ...)
        currentMethod[0].accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodCallExpr call, Void arg) {
                super.visit(call, arg);
                if (call.getNameAsString().equals("requireNonNull")) {
                    call.getScope().ifPresent(scope -> {
                        if (scope.toString().equals("Objects") || scope.toString().equals("java.util.Objects")) {
                            // 检查参数是否包含目标变量
                            if (!call.getArguments().isEmpty()) {
                                String firstArg = call.getArguments().get(0).toString();
                                if (firstArg.equals(varName)) {
                                    hasCheck[0] = true;
                                }
                            }
                        }
                    });
                }
            }
        }, null);

        return hasCheck[0];
    }

    /**
     * 检查是否存在空指针检查
     * 现在同时检查变量名和完整作用域
     */
    private boolean hasNullCheck(String variableName, String fullScope, CompilationUnit compilationUnit, MethodCallExpr currentExpr) {
        // 获取当前方法
        final com.github.javaparser.ast.body.MethodDeclaration[] currentMethod = {null};
        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration method, Void arg) {
                super.visit(method, arg);
                if (method.getBegin().isPresent() && currentExpr.getBegin().isPresent()) {
                    int methodStart = method.getBegin().get().line;
                    int methodEnd = method.getEnd().get().line;
                    int exprLine = currentExpr.getBegin().get().line;
                    if (exprLine >= methodStart && exprLine <= methodEnd) {
                        currentMethod[0] = method;
                    }
                }
            }
        }, null);

        if (currentMethod[0] == null) {
            return false;
        }

        // 收集该方法中所有已检查非null的变量
        Set<String> checkedVars = new HashSet<>();
        
        currentMethod[0].accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(IfStmt ifStmt, Void arg) {
                super.visit(ifStmt, arg);
                String condition = ifStmt.getCondition().toString();
                
                // 检查 if (var != null) 模式
                if (condition.contains("!=") && condition.contains("null")) {
                    // 提取变量名
                    String checked = condition.replace(" ", "").replace("!=null", "").replace("!=null", "");
                    if (checked.equals(variableName) || fullScope.contains(checked)) {
                        checkedVars.add(checked);
                    }
                }
                // 检查 if (var == null) return; 模式
                if (condition.contains("==") && condition.contains("null")) {
                    String checked = condition.replace(" ", "").replace("==null", "");
                    if (checked.equals(variableName) || fullScope.contains(checked)) {
                        // 检查是否紧跟return或抛出异常
                        if (ifStmt.getThenStmt().toString().contains("return") ||
                            ifStmt.getThenStmt().toString().contains("throw")) {
                            checkedVars.add(checked);
                        }
                    }
                }
            }
        }, null);

        // 检查当前表达式之前是否有null检查
        int currentLine = currentExpr.getBegin().map(b -> b.line).orElse(0);
        final boolean[] hasPriorCheck = {false};
        
        currentMethod[0].accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(IfStmt ifStmt, Void arg) {
                super.visit(ifStmt, arg);
                int ifLine = ifStmt.getEnd().map(e -> e.line).orElse(0);
                if (ifLine < currentLine) {
                    String condition = ifStmt.getCondition().toString();
                    if ((condition.contains(variableName) || condition.contains(fullScope)) &&
                        condition.contains("null")) {
                        hasPriorCheck[0] = true;
                    }
                }
            }
        }, null);

        return hasPriorCheck[0] || checkedVars.contains(variableName);
    }
}
