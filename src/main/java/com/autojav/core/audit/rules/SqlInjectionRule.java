package com.autojav.core.audit.rules;

import com.autojav.core.audit.AuditResult;
import com.autojav.core.audit.AuditRule;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlInjectionRule implements AuditRule {

    @Override
    public String getName() {
        return "SQL注入检查";
    }

    @Override
    public String getDescription() {
        return "检查可能导致SQL注入的代码，如直接拼接SQL语句而不使用PreparedStatement";
    }

    @Override
    public List<AuditResult> audit(CompilationUnit compilationUnit) {
        List<AuditResult> results = new ArrayList<>();
        String filePath = compilationUnit.getStorage().map(s -> s.getPath().toString()).orElse("unknown");

        // 第一轮：收集所有可疑的SQL变量（包含SQL关键字且有字符串拼接）
        Map<String, Integer> suspiciousSqlVars = new HashMap<>();
        Set<String> safeSqlVars = new HashSet<>();

        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(VariableDeclarationExpr varDecl, Void arg) {
                super.visit(varDecl, arg);
                
                varDecl.getVariables().forEach(var -> {
                    if (var.getInitializer().isPresent()) {
                        String varName = var.getNameAsString();
                        var initializer = var.getInitializer().get();
                        
                        // 检查是否是字符串拼接
                        if (isStringConcatenation(initializer)) {
                            String initStr = initializer.toString();
                            if (containsSqlKeywords(initStr)) {
                                // 检查是否使用了变量（用户输入）
                                if (containsVariableReference(initializer)) {
                                    int line = varDecl.getBegin().map(b -> b.line).orElse(0);
                                    suspiciousSqlVars.put(varName, line);
                                }
                            }
                        }
                        
                        // 检查是否是安全的预编译SQL（使用 ? 占位符）
                        if (initializer.isStringLiteralExpr()) {
                            String sql = initializer.asStringLiteralExpr().getValue();
                            if (containsSqlKeywords(sql) && sql.contains("?")) {
                                safeSqlVars.add(varName);
                            }
                        }
                    }
                });
            }
        }, null);

        // 第二轮：检查SQL执行方法调用
        compilationUnit.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodCallExpr methodCallExpr, Void arg) {
                super.visit(methodCallExpr, arg);

                String methodName = methodCallExpr.getNameAsString();
                if (isSqlExecutionMethod(methodName)) {
                    int line = methodCallExpr.getBegin().map(b -> b.line).orElse(0);
                    int column = methodCallExpr.getBegin().map(b -> b.column).orElse(0);

                    // 检查每个参数
                    methodCallExpr.getArguments().forEach(argExpr -> {
                        // 情况1：直接使用字符串拼接
                        if (isStringConcatenation(argExpr) && containsSqlKeywords(argExpr.toString())) {
                            if (containsVariableReference(argExpr)) {
                                results.add(new AuditResult(
                                        getName(),
                                        Severity.ERROR,
                                        "SQL注入风险：直接拼接用户输入到SQL语句",
                                        filePath,
                                        line,
                                        column,
                                        "使用PreparedStatement参数化查询替代字符串拼接"
                                ));
                            }
                        }
                        
                        // 情况2：使用变量传递SQL（可能是前面拼接好的）
                        if (argExpr.isNameExpr()) {
                            String varName = argExpr.asNameExpr().getNameAsString();
                            if (suspiciousSqlVars.containsKey(varName) && !safeSqlVars.contains(varName)) {
                                results.add(new AuditResult(
                                        getName(),
                                        Severity.ERROR,
                                        "SQL注入风险：SQL语句通过变量拼接了用户输入",
                                        filePath,
                                        line,
                                        column,
                                        "变量 '" + varName + "' 在 line " + suspiciousSqlVars.get(varName) + " 处拼接了用户输入，建议使用PreparedStatement参数化查询"
                                ));
                            }
                        }
                        
                        // 情况3：使用Statement.execute*方法（而非PreparedStatement）
                        methodCallExpr.getScope().ifPresent(scope -> {
                            if (scope.toString().equals("stmt") || scope.toString().endsWith("Statement")) {
                                // 如果是Statement而不是PreparedStatement，且参数不是纯字符串字面量
                                if (!argExpr.isStringLiteralExpr() || isStringConcatenation(argExpr)) {
                                    boolean alreadyReported = results.stream()
                                            .anyMatch(r -> r.getLine() == line && r.getMessage().contains("SQL注入"));
                                    if (!alreadyReported) {
                                        results.add(new AuditResult(
                                                getName(),
                                                Severity.WARNING,
                                                "潜在SQL注入风险：使用Statement执行动态SQL",
                                                filePath,
                                                line,
                                                column,
                                                "建议使用PreparedStatement替代Statement，并使用参数化查询"
                                        ));
                                    }
                                }
                            }
                        });
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
     */
    private boolean isSqlExecutionMethod(String methodName) {
        return methodName.equals("execute") || 
               methodName.equals("executeQuery") || 
               methodName.equals("executeUpdate") || 
               methodName.equals("executeBatch");
    }

    /**
     * 检查字符串是否包含SQL关键字
     */
    private boolean containsSqlKeywords(String sql) {
        String[] keywords = {"SELECT", "INSERT", "UPDATE", "DELETE", "FROM", "WHERE", "AND", "OR"};
        String upperSql = sql.toUpperCase();
        for (String keyword : keywords) {
            if (upperSql.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查表达式是否包含字符串拼接（+操作符）
     */
    private boolean isStringConcatenation(com.github.javaparser.ast.expr.Expression expr) {
        if (expr.isBinaryExpr()) {
            BinaryExpr binary = expr.asBinaryExpr();
            if (binary.getOperator() == BinaryExpr.Operator.PLUS) {
                return true;
            }
            // 递归检查左右两边
            return isStringConcatenation(binary.getLeft()) || 
                   isStringConcatenation(binary.getRight());
        }
        return false;
    }

    /**
     * 检查表达式中是否包含变量引用（用户输入）
     */
    private boolean containsVariableReference(com.github.javaparser.ast.expr.Expression expr) {
        final boolean[] found = {false};
        expr.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(NameExpr nameExpr, Void arg) {
                super.visit(nameExpr, arg);
                // 排除常见的SQL关键字和大写常量
                String name = nameExpr.getNameAsString();
                if (!name.equals("SELECT") && !name.equals("FROM") && !name.equals("WHERE") &&
                    !name.equals("AND") && !name.equals("OR") && !name.equals("INSERT") &&
                    !name.equals("UPDATE") && !name.equals("DELETE") &&
                    !name.toUpperCase().equals(name)) {  // 假设用户输入变量不是全大写
                    found[0] = true;
                }
            }
        }, null);
        return found[0];
    }
}
