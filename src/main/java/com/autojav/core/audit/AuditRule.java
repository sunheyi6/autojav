package com.autojav.core.audit;

import com.github.javaparser.ast.CompilationUnit;

import java.util.List;

public interface AuditRule {

    /**
     * 获取规则名称
     * @return 规则名称
     */
    String getName();

    /**
     * 获取规则描述
     * @return 规则描述
     */
    String getDescription();

    /**
     * 执行审计
     * @param compilationUnit 编译单元
     * @return 审计结果列表
     */
    List<AuditResult> audit(CompilationUnit compilationUnit);

    /**
     * 获取规则严重程度
     * @return 严重程度
     */
    Severity getSeverity();

    /**
     * 严重程度枚举
     */
    enum Severity {
        ERROR(1),
        WARNING(2),
        INFO(3);

        private final int level;

        Severity(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    }
}
