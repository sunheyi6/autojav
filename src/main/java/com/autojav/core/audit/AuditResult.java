package com.autojav.core.audit;

import lombok.Data;

@Data
public class AuditResult {

    /**
     * 规则名称
     */
    private String ruleName;

    /**
     * 严重程度
     */
    private AuditRule.Severity severity;

    /**
     * 问题描述
     */
    private String message;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 行号
     */
    private int line;

    /**
     * 列号
     */
    private int column;

    /**
     * 修复建议
     */
    private String fixSuggestion;

    public AuditResult(String ruleName, AuditRule.Severity severity, String message, String filePath, int line, int column, String fixSuggestion) {
        this.ruleName = ruleName;
        this.severity = severity;
        this.message = message;
        this.filePath = filePath;
        this.line = line;
        this.column = column;
        this.fixSuggestion = fixSuggestion;
    }

    public AuditResult(String ruleName, AuditRule.Severity severity, String message, String filePath, int line, int column) {
        this(ruleName, severity, message, filePath, line, column, null);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(severity).append("] ");
        sb.append(ruleName).append(": ");
        sb.append(message).append(" ");
        sb.append("(").append(filePath).append(":").append(line).append(":").append(column).append(")");
        if (fixSuggestion != null) {
            sb.append(" 建议: ").append(fixSuggestion);
        }
        return sb.toString();
    }
}
