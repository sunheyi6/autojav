package com.autojav.core.audit.rules;

import com.autojav.core.audit.AuditResult;
import com.autojav.core.audit.AuditRule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class NullPointerExceptionRule implements AuditRule {

    @Override
    public String getName() {
        return "空指针检查"; // 基于阿里巴巴Java规范：避免空指针异常
    }

    @Override
    public String getDescription() {
        return "检查可能导致空指针异常的代码，如直接调用可能为null的对象方法或访问其属性"; // 基于阿里巴巴Java规范：避免空指针异常
    }

    @Override
    public List<AuditResult> audit(CompilationUnit compilationUnit) {
        List<AuditResult> results = new ArrayList<>();
        String filePath = compilationUnit.getStorage().map(s -> s.getPath().toString()).orElse("unknown");

        // 检查方法调用，寻找可能的空指针风险
        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodCallExpr methodCallExpr, Void arg) {
                super.visit(methodCallExpr, arg);

                // 检查方法调用的对象是否可能为null
                if (methodCallExpr.getScope().isPresent()) {
                    // 简单检查：如果作用域是一个NameExpr，可能存在空指针风险
                    if (methodCallExpr.getScope().get() instanceof NameExpr) {
                        NameExpr nameExpr = (NameExpr) methodCallExpr.getScope().get();
                        // 检查是否有对该变量的空检查
                        boolean hasNullCheck = hasNullCheck(nameExpr.getNameAsString(), compilationUnit);
                        if (!hasNullCheck) {
                            int line = methodCallExpr.getBegin().map(b -> b.line).orElse(0);
                            int column = methodCallExpr.getBegin().map(b -> b.column).orElse(0);
                            results.add(new AuditResult(
                                    getName(),
                                    Severity.WARNING,
                                    "可能存在空指针异常风险：直接调用" + nameExpr.getNameAsString() + "的方法", // 基于阿里巴巴Java规范：避免空指针异常
                                    filePath,
                                    line,
                                    column,
                                    "建议在调用方法前添加空指针检查"
                            ));
                        }
                    }
                }
            }
        }, null);

        return results;
    }

    @Override
    public Severity getSeverity() {
        return Severity.WARNING;
    }

    /**
     * 检查是否存在空指针检查
     * @param variableName 变量名
     * @param compilationUnit 编译单元
     * @return 是否存在空指针检查
     */
    private boolean hasNullCheck(String variableName, CompilationUnit compilationUnit) {
        List<IfStmt> ifStmts = new ArrayList<>();
        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(IfStmt ifStmt, Void arg) {
                super.visit(ifStmt, arg);
                ifStmts.add(ifStmt);
            }
        }, null);

        // 简单检查：是否存在 if (variable != null) 这样的语句
        for (IfStmt ifStmt : ifStmts) {
            String condition = ifStmt.getCondition().toString();
            if (condition.contains(variableName) && condition.contains("!=") && condition.contains("null")) {
                return true;
            }
        }

        return false;
    }
}
