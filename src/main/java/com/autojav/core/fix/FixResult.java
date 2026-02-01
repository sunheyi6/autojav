package com.autojav.core.fix;

import lombok.Data;

import java.io.File;

@Data
public class FixResult {

    /**
     * 修复的文件
     */
    private File file;

    /**
     * 原始代码
     */
    private String originalCode;

    /**
     * 修复后的代码
     */
    private String fixedCode;

    /**
     * 是否仅预览
     */
    private boolean preview;

    /**
     * 是否应用修复
     */
    private boolean applied;

    /**
     * 备份文件
     */
    private File backupFile;

    public FixResult(File file, String originalCode, String fixedCode, boolean preview, boolean applied) {
        this.file = file;
        this.originalCode = originalCode;
        this.fixedCode = fixedCode;
        this.preview = preview;
        this.applied = applied;
    }

    public FixResult(File file, String originalCode, String fixedCode, boolean preview, boolean applied, File backupFile) {
        this(file, originalCode, fixedCode, preview, applied);
        this.backupFile = backupFile;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("修复结果:\n");
        sb.append("文件: ").append(file.getName()).append("\n");
        sb.append("状态: ").append(applied ? "已应用" : "预览").append("\n");
        if (backupFile != null) {
            sb.append("备份文件: ").append(backupFile.getName()).append("\n");
        }
        sb.append("修复前后代码差异: \n");
        // 简单的差异显示
        String[] originalLines = originalCode.split("\\n");
        String[] fixedLines = fixedCode.split("\\n");
        int maxLines = Math.max(originalLines.length, fixedLines.length);
        for (int i = 0; i < maxLines; i++) {
            if (i < originalLines.length && i < fixedLines.length) {
                if (!originalLines[i].equals(fixedLines[i])) {
                    sb.append("- ").append(originalLines[i]).append("\n");
                    sb.append("+ ").append(fixedLines[i]).append("\n");
                } else {
                    sb.append("  ").append(originalLines[i]).append("\n");
                }
            } else if (i < originalLines.length) {
                sb.append("- ").append(originalLines[i]).append("\n");
            } else {
                sb.append("+ ").append(fixedLines[i]).append("\n");
            }
        }
        return sb.toString();
    }
}
