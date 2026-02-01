package com.autojav.core.audit.rules;

import com.autojav.core.audit.AuditResult;
import com.autojav.core.audit.AuditRule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class SqlInjectionRule implements AuditRule {

    @Override
    public String getName() {
        return "SQL注入检查"; // 基于阿里巴巴Java规范：防止SQL注入
    }

    @Override
    public String getDescription() {
        return "检查可能导致SQL注入的代码，如直接拼接SQL语句而不使用PreparedStatement"; // 基于阿里巴巴Java规范：防止SQL注入
    }

    @Override
    public List<AuditResult> audit(CompilationUnit compilationUnit) {
        List<AuditResult> results = new ArrayList<>();
        String filePath = compilationUnit.getStorage().map(s -> s.getPath().toString()).orElse("unknown");

        // 检查SQL语句拼接，寻找可能的SQL注入风险
        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodCallExpr methodCallExpr, Void arg) {
                super.visit(methodCallExpr, arg);

                // 检查常见的SQL执行方法
                String methodName = methodCallExpr.getNameAsString();
                if (isSqlExecutionMethod(methodName)) {
                    // 检查方法参数，寻找字符串拼接的SQL
                    methodCallExpr.getArguments().forEach(argExpr -> {
                        if (argExpr.isStringLiteralExpr()) {
                            StringLiteralExpr stringLiteral = argExpr.asStringLiteralExpr();
                            String sql = stringLiteral.getValue();
                            if (containsSqlKeywords(sql) && containsConcatenation(methodCallExpr)) {
                                int line = methodCallExpr.getBegin().map(b -> b.line).orElse(0);
                                int column = methodCallExpr.getBegin().map(b -> b.column).orElse(0);
                                results.add(new AuditResult(
                                        getName(),
                                        Severity.ERROR,
                                        "可能存在SQL注入风险：直接拼接SQL语句", // 基于阿里巴巴Java规范：防止SQL注入
                                        filePath,
                                        line,
                                        column,
                                        "建议使用PreparedStatement参数化查询"
                                ));
                            }
                        }
                    });
                }
            }
        }, null);

        return results;
    }

    @Override
    public Severity getSeverity() {
        return Severity.ERROR;
    }

    /**
     * 检查是否为SQL执行方法
     * @param methodName 方法名
     * @return 是否为SQL执行方法
     */
    private boolean isSqlExecutionMethod(String methodName) {
        String[] sqlMethods = {"execute", "executeQuery", "executeUpdate", "executeBatch"};
        for (String method : sqlMethods) {
            if (methodName.equals(method)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查字符串是否包含SQL关键字
     * @param sql SQL语句
     * @return 是否包含SQL关键字
     */
    private boolean containsSqlKeywords(String sql) {
        String[] keywords = {"SELECT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE", "AND", "OR", "ORDER", "GROUP", "JOIN"};
        String upperSql = sql.toUpperCase();
        for (String keyword : keywords) {
            if (upperSql.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查方法调用是否包含字符串拼接
     * @param methodCallExpr 方法调用表达式
     * @return 是否包含字符串拼接
     */
    private boolean containsConcatenation(MethodCallExpr methodCallExpr) {
        // 简单检查：如果方法调用的参数中包含"+"操作符，可能存在字符串拼接
        return methodCallExpr.toString().contains("+");
    }
}
