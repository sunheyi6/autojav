package com.autojav.core.fix;

import com.autojav.core.ai.AIService;
import com.autojav.core.ai.AIServiceFactory;
import com.autojav.core.ai.AIException;
import com.autojav.core.TerminalUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class FixManager {

    private AIService aiService;

    public FixManager(String apiKey) {
        this.aiService = AIServiceFactory.createAIService(apiKey);
    }

    public FixManager(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * 修复代码
     * @param file Java文件
     * @param issues 问题描述
     * @param preview 是否仅预览
     * @return 修复结果
     * @throws Exception 异常
     */
    public FixResult fixCode(File file, String issues, boolean preview) throws Exception {
        // 读取文件内容
        String originalCode = Files.readString(file.toPath());

        try {
            // 生成修复后的代码
            String fixedCode = aiService.fixCode(originalCode, issues);

            // 提取修复后的代码（去除可能的标记和注释）
            fixedCode = extractCode(fixedCode);

            if (preview) {
                // 仅预览，不修改文件
                return new FixResult(file, originalCode, fixedCode, true, false);
            } else {
                // 创建备份文件
                File backupFile = createBackupFile(file);

                // 写入修复后的代码
                Files.writeString(file.toPath(), fixedCode);

                return new FixResult(file, originalCode, fixedCode, false, true, backupFile);
            }
        } catch (AIException e) {
            log.error("AI修复失败", e);
            throw new Exception("AI修复失败: " + e.getMessage(), e);
        } catch (IOException e) {
            log.error("文件操作失败", e);
            throw new Exception("文件操作失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建备份文件
     * @param originalFile 原始文件
     * @return 备份文件
     * @throws IOException IO异常
     */
    private File createBackupFile(File originalFile) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String backupFileName = originalFile.getName().replace(".java", ".backup." + timestamp + ".java");
        File backupFile = new File(originalFile.getParent(), backupFileName);

        // 复制文件
        Files.copy(originalFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        log.info("创建备份文件: {}", backupFile.getAbsolutePath());
        return backupFile;
    }

    /**
     * 提取代码内容，去除可能的标记和注释
     * @param content 原始内容
     * @return 提取的代码
     */
    private String extractCode(String content) {
        // 简单实现：去除 ```java 和 ``` 标记
        content = content.replaceAll("```java\\n", "");
        content = content.replaceAll("```\\n", "");
        content = content.replaceAll("```", "");
        return content.trim();
    }

    /**
     * 显示修复预览
     * @param result 修复结果
     */
    public void showPreview(FixResult result) {
        TerminalUtils.printInfo("修复预览:");
        TerminalUtils.printInfo("文件: " + result.getFile().getName());
        TerminalUtils.printInfo("\n原始代码:");
        TerminalUtils.printInfo(result.getOriginalCode());
        TerminalUtils.printInfo("\n修复后代码:");
        TerminalUtils.printInfo(result.getFixedCode());
    }

    /**
     * 回滚修复
     * @param result 修复结果
     * @throws IOException IO异常
     */
    public void rollback(FixResult result) throws IOException {
        if (result.isApplied() && result.getBackupFile() != null) {
            // 从备份文件恢复
            Files.copy(result.getBackupFile().toPath(), result.getFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            TerminalUtils.printSuccess("修复已回滚，文件已恢复到原始状态");
            log.info("回滚修复: {}", result.getFile().getAbsolutePath());
        } else {
            TerminalUtils.printError("无法回滚，修复未应用或没有备份文件");
        }
    }
}
