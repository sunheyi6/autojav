package com.autojav.core.audit;

import com.autojav.core.audit.rules.NullPointerExceptionRule;
import com.autojav.core.audit.rules.SqlInjectionRule;
import com.github.javaparser.ast.CompilationUnit;

import java.util.ArrayList;
import java.util.List;

public class AuditManager {

    private List<AuditRule> rules;

    public AuditManager() {
        rules = new ArrayList<>();
        // 初始化审计规则
        initRules();
    }

    /**
     * 初始化审计规则
     */
    private void initRules() {
        rules.add(new NullPointerExceptionRule());
        rules.add(new SqlInjectionRule());
        // 后续可以添加更多规则
    }

    /**
     * 执行审计
     * @param compilationUnit 编译单元
     * @return 审计结果列表
     */
    public List<AuditResult> audit(CompilationUnit compilationUnit) {
        List<AuditResult> results = new ArrayList<>();
        for (AuditRule rule : rules) {
            results.addAll(rule.audit(compilationUnit));
        }
        return results;
    }

    /**
     * 执行审计（多个编译单元）
     * @param compilationUnits 编译单元列表
     * @return 审计结果列表
     */
    public List<AuditResult> audit(List<CompilationUnit> compilationUnits) {
        List<AuditResult> results = new ArrayList<>();
        for (CompilationUnit compilationUnit : compilationUnits) {
            results.addAll(audit(compilationUnit));
        }
        return results;
    }

    /**
     * 获取所有审计规则
     * @return 审计规则列表
     */
    public List<AuditRule> getRules() {
        return rules;
    }

    /**
     * 添加审计规则
     * @param rule 审计规则
     */
    public void addRule(AuditRule rule) {
        rules.add(rule);
    }

    /**
     * 移除审计规则
     * @param rule 审计规则
     */
    public void removeRule(AuditRule rule) {
        rules.remove(rule);
    }
}
