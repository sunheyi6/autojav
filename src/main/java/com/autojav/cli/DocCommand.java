package com.autojav.cli;

import com.autojav.core.CodeParser;
import com.autojav.core.TerminalUtils;
import com.autojav.core.doc.DocGenerationException;
import com.autojav.core.doc.DocGenerator;
import com.autojav.core.doc.DocGeneratorFactory;
import com.autojav.core.license.LicenseManager;
import com.github.javaparser.ast.CompilationUnit;
import picocli.CommandLine;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "doc",
        description = "自动生成接口文档"
)
public class DocCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "Java文件或目录路径")
    private String path;

    @CommandLine.Option(names = {"-f", "--format"}, defaultValue = "markdown", description = "文档格式: markdown, openapi")
    private String format;

    @CommandLine.Option(names = {"-o", "--output"}, description = "文档输出文件或目录")
    private String output;

    @CommandLine.Option(names = {"-t", "--template"}, description = "自定义模板文件")
    private String template;

    @CommandLine.Option(names = {"-r", "--recursive"}, description = "递归处理目录")
    private boolean recursive;

    private LicenseManager licenseManager;

    @Override
    public Integer call() throws Exception {
        licenseManager = new LicenseManager();

        TerminalUtils.printInfo("开始生成文档: " + path);
        TerminalUtils.printInfo("文档格式: " + format);
        TerminalUtils.printInfo("递归处理: " + recursive);
        if (output != null) {
            TerminalUtils.printInfo("输出路径: " + output);
        }
        if (template != null) {
            TerminalUtils.printInfo("自定义模板: " + template);
        }

        // 检查自定义模板功能是否可用（免费版限制）
        if (template != null) {
            if (!licenseManager.checkAndApplyRestriction("custom.template")) {
                return 1;
            }
        }

        // 创建代码解析器
        CodeParser parser = new CodeParser();
        File target = new File(path);

        // 解析Java文件或目录
        List<CompilationUnit> compilationUnits;
        if (target.isFile()) {
            CompilationUnit cu = parser.parseFile(target);
            if (cu != null) {
                compilationUnits = List.of(cu);
                TerminalUtils.printSuccess("文件解析成功: " + target.getName());
            } else {
                TerminalUtils.printError("文件解析失败: " + target.getName());
                return 1;
            }
        } else if (target.isDirectory()) {
            compilationUnits = parser.parseDirectory(target, recursive);
            TerminalUtils.printSuccess("目录解析完成，共解析 " + compilationUnits.size() + " 个Java文件");
        } else {
            TerminalUtils.printError("无效的路径: " + path);
            return 1;
        }

        // 创建文档生成器
        DocGenerator docGenerator = DocGeneratorFactory.createDocGenerator(format);

        // 输出文档
        if (output != null) {
            File outputFile = new File(output);
            // 确保输出目录存在
            if (outputFile.getParentFile() != null && !outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            // 保存文档
            try {
                docGenerator.generateAndSave(compilationUnits, outputFile);
                TerminalUtils.printSuccess("文档已保存到: " + outputFile.getAbsolutePath());
            } catch (DocGenerationException e) {
                TerminalUtils.printError("保存文档失败: " + e.getMessage());
                return 1;
            }
        } else {
            // 直接输出到控制台
            String docContent;
            try {
                docContent = docGenerator.generate(compilationUnits);
                TerminalUtils.printSuccess("文档生成成功");
                System.out.println(docContent);
            } catch (DocGenerationException e) {
                TerminalUtils.printError("文档生成失败: " + e.getMessage());
                return 1;
            }
        }

        return 0;
    }
}
