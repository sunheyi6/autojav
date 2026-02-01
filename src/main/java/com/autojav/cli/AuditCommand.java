package com.autojav.cli;

import com.autojav.core.CodeParser;
import com.autojav.core.ConfigManager;
import com.autojav.core.TerminalUtils;
import com.autojav.core.ai.AIService;
import com.autojav.core.ai.AIServiceFactory;
import com.autojav.core.audit.AuditManager;
import com.autojav.core.audit.AuditResult;
import com.autojav.core.fix.FixManager;
import com.autojav.core.fix.FixResult;
import com.autojav.core.license.LicenseManager;
import com.github.javaparser.ast.CompilationUnit;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;

;

@CommandLine.Command(
        name = "audit",
        description = "代码审计与自动修复"
)
public class AuditCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "Java文件或目录路径")
    private String path;

    @CommandLine.Option(names = {"-r", "--recursive"}, description = "递归处理目录")
    private boolean recursive;

    @CommandLine.Option(names = {"-f", "--fix"}, description = "自动修复问题")
    private boolean fix;

    @CommandLine.Option(names = {"-o", "--output"}, description = "审计结果输出文件")
    private String output;

    @CommandLine.Option(names = {"-ai", "--use-ai"}, description = "使用AI进行深度审计")
    private boolean useAI;

    @CommandLine.Option(names = {"-p", "--preview"}, description = "预览修复结果，不实际修改文件")
    private boolean preview;

    private LicenseManager licenseManager;

    @Override
    public Integer call() throws Exception {
        licenseManager = new LicenseManager();

        TerminalUtils.printInfo("开始审计: " + path);
        TerminalUtils.printInfo("递归处理: " + recursive);
        TerminalUtils.printInfo("自动修复: " + fix);
        TerminalUtils.printInfo("使用AI: " + useAI);
        TerminalUtils.printInfo("预览修复: " + preview);
        if (output != null) {
            TerminalUtils.printInfo("输出文件: " + output);
        }

        // 检查权限
        if (useAI && !licenseManager.checkAndApplyRestriction("ai.audit")) {
            return 1;
        }

        if (fix && !licenseManager.checkAndApplyRestriction("code.fix")) {
            return 1;
        }

        CodeParser parser = new CodeParser();
        AuditManager auditManager = new AuditManager();
        File target = new File(path);

        if (target.isFile()) {
            // 解析单个文件
            CompilationUnit cu = parser.parseFile(target);
            if (cu != null) {
                TerminalUtils.printSuccess("文件解析成功: " + target.getName());
                TerminalUtils.printInfo("类数量: " + parser.extractClasses(cu).size());
                TerminalUtils.printInfo("方法数量: " + parser.extractMethods(cu).size());

                // 执行本地审计
                List<AuditResult> results = auditManager.audit(cu);
                printAuditResults(results);

                // 使用AI进行深度审计
                if (useAI) {
                    performAIAudit(target);
                }

                // 自动修复问题
                if (fix) {
                    performFix(target, results);
                }
            } else {
                TerminalUtils.printError("文件解析失败: " + target.getName());
            }
        } else if (target.isDirectory()) {
            // 解析目录
            List<CompilationUnit> compilationUnits = parser.parseDirectory(target, recursive);
            TerminalUtils.printSuccess("目录解析完成，共解析 " + compilationUnits.size() + " 个Java文件");

            // 执行本地审计
            List<AuditResult> results = auditManager.audit(compilationUnits);
            printAuditResults(results);

            // 使用AI进行深度审计（仅对第一个文件，避免API调用过多）
            if (useAI && !compilationUnits.isEmpty()) {
                File firstFile = compilationUnits.get(0).getStorage().map(s -> s.getPath().toFile()).orElse(null);
                if (firstFile != null) {
                    performAIAudit(firstFile);
                }
            }

            // 自动修复问题（仅对第一个文件，避免API调用过多）
            if (fix && !compilationUnits.isEmpty()) {
                File firstFile = compilationUnits.get(0).getStorage().map(s -> s.getPath().toFile()).orElse(null);
                if (firstFile != null) {
                    // 过滤出该文件的问题
                    List<AuditResult> fileResults = results.stream()
                            .filter(r -> r.getFilePath().contains(firstFile.getName()))
                            .collect(java.util.stream.Collectors.toList());
                    performFix(firstFile, fileResults);
                }
            }
        } else {
            TerminalUtils.printError("无效的路径: " + path);
            return 1;
        }

        return 0;
    }

    /**
     * 执行AI审计
     * @param file Java文件
     */
    private void performAIAudit(File file) throws Exception {
        TerminalUtils.printInfo("开始AI深度审计...");

        // 加载API密钥
        ConfigManager configManager = new ConfigManager();
        String apiKey = configManager.get("ai.api.key");

        if (apiKey == null || apiKey.isEmpty()) {
            TerminalUtils.printError("未配置AI API密钥，请先使用 config set ai.api.key <your-api-key> 设置");
            return;
        }

        // 读取文件内容
        String code = Files.readString(file.toPath());

        // 创建AI服务
        AIService aiService = AIServiceFactory.createAIService(apiKey);

        // 定义审计规则
        String rules = "基于阿里巴巴Java开发规范，检查以下问题：\n" +
                "1. 空指针异常风险\n" +
                "2. SQL注入风险\n" +
                "3. 代码规范问题\n" +
                "4. 性能问题\n" +
                "5. 安全问题";

        try {
            // 执行AI审计
            String aiResult = aiService.auditCode(code, rules);
            TerminalUtils.printSuccess("AI审计完成:");
            TerminalUtils.printInfo(aiResult);
        } catch (Exception e) {
            TerminalUtils.printError("AI审计失败: " + e.getMessage());
        }
    }

    /**
     * 执行代码修复
     * @param file Java文件
     * @param results 审计结果
     */
    private void performFix(File file, List<AuditResult> results) throws Exception {
        if (results.isEmpty()) {
            TerminalUtils.printSuccess("没有发现需要修复的问题");
            return;
        }

        TerminalUtils.printInfo("开始代码修复...");

        // 加载API密钥
        ConfigManager configManager = new ConfigManager();
        String apiKey = configManager.get("ai.api.key");

        if (apiKey == null || apiKey.isEmpty()) {
            TerminalUtils.printError("未配置AI API密钥，请先使用 config set ai.api.key <your-api-key> 设置");
            return;
        }

        // 构建问题描述
        StringBuilder issuesBuilder = new StringBuilder();
        for (AuditResult result : results) {
            issuesBuilder.append("- ").append(result.getMessage()).append("\n");
        }
        String issues = issuesBuilder.toString();

        // 创建修复管理器
        FixManager fixManager = new FixManager(apiKey);

        // 执行修复
        FixResult fixResult = fixManager.fixCode(file, issues, preview);

        // 显示修复结果
        fixManager.showPreview(fixResult);

        // 如果不是预览，并且用户确认，应用修复
        if (!preview) {
            TerminalUtils.printInfo("是否应用修复？(y/n): ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("y")) {
                // 修复已经在fixCode方法中应用
                TerminalUtils.printSuccess("修复已应用，备份文件: " + fixResult.getBackupFile().getName());
            } else {
                // 回滚修复
                fixManager.rollback(fixResult);
                TerminalUtils.printInfo("修复已取消");
            }
        }
    }

    /**
     * 打印审计结果
     * @param results 审计结果列表
     */
    private void printAuditResults(List<AuditResult> results) {
        if (results.isEmpty()) {
            TerminalUtils.printSuccess("未发现问题");
            return;
        }

        TerminalUtils.printInfo("审计结果（共 " + results.size() + " 个问题）:");
        for (AuditResult result : results) {
            switch (result.getSeverity()) {
                case ERROR:
                    TerminalUtils.printError(result.toString());
                    break;
                case WARNING:
                    TerminalUtils.printWarning(result.toString());
                    break;
                case INFO:
                    TerminalUtils.printInfo(result.toString());
                    break;
            }
        }
    }
}
