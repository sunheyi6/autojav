package com.autojav.cli;

import com.autojav.core.CodeParser;
import com.autojav.core.ConfigManager;
import com.autojav.core.TerminalUtils;
import com.autojav.core.audit.AuditManager;
import com.autojav.core.audit.AuditResult;
import com.github.javaparser.ast.CompilationUnit;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "audit",
        description = "代码审计"
)
public class AuditCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "Java文件或目录路径")
    private String path;

    @CommandLine.Option(names = {"-r", "--recursive"}, description = "递归处理目录")
    private boolean recursive;

    @CommandLine.Option(names = {"-o", "--output"}, description = "审计结果输出文件")
    private String output;

    @Override
    public Integer call() throws Exception {
        TerminalUtils.printInfo("开始审计: " + path);
        TerminalUtils.printInfo("递归处理: " + recursive);
        if (output != null) {
            TerminalUtils.printInfo("输出文件: " + output);
        }

        CodeParser parser = new CodeParser();
        AuditManager auditManager = new AuditManager();
        File target = new File(path);

        if (target.isFile()) {
            CompilationUnit cu = parser.parseFile(target);
            if (cu != null) {
                TerminalUtils.printSuccess("文件解析成功: " + target.getName());
                TerminalUtils.printInfo("类数量: " + parser.extractClasses(cu).size());
                TerminalUtils.printInfo("方法数量: " + parser.extractMethods(cu).size());

                List<AuditResult> results = auditManager.audit(cu);
                printAuditResults(results);
            } else {
                TerminalUtils.printError("文件解析失败: " + target.getName());
            }
        } else if (target.isDirectory()) {
            List<CompilationUnit> compilationUnits = parser.parseDirectory(target, recursive);
            TerminalUtils.printSuccess("目录解析完成，共解析 " + compilationUnits.size() + " 个Java文件");
            List<AuditResult> results = auditManager.audit(compilationUnits);
            printAuditResults(results);
        } else {
            File checkFile = new File(path);
            if (!checkFile.exists()) {
                TerminalUtils.printError("路径不存在: " + path);
                TerminalUtils.printInfo("当前工作目录: " + System.getProperty("user.dir"));
                TerminalUtils.printInfo("提示: 请提供有效的Java文件或目录路径");
                TerminalUtils.printInfo("示例: java -jar autojav-cli.jar audit /path/to/YourFile.java");
            } else {
                TerminalUtils.printError("无效的路径（不是文件也不是目录）: " + path);
            }
            return 1;
        }

        return 0;
    }

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
