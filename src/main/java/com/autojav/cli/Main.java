package com.autojav.cli;

import com.autojav.core.ExceptionHandler;

import picocli.CommandLine;

import java.util.concurrent.Callable;

@picocli.CommandLine.Command(
        name = "autojav",
        version = "1.0.0",
        description = "Java代码审计CLI工具",
        subcommands = {
                AuditCommand.class,
                DocCommand.class,
                ConfigCommand.class
        }
)
public class Main implements Callable<Integer> {

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "显示帮助信息")
    private boolean help;

    @CommandLine.Option(names = {"-v", "--version"}, versionHelp = true, description = "显示版本信息")
    private boolean version;

    @Override
    public Integer call() throws Exception {
        CommandLine.usage(this, System.out);
        return 0;
    }

    public static void main(String[] args) {
        try {
            int exitCode = new CommandLine(new Main()).execute(args);
            System.exit(exitCode);
        } catch (RuntimeException e) {
            int exitCode = ExceptionHandler.handleRuntimeException(e);
            System.exit(exitCode);
        } catch (Exception e) {
            int exitCode = ExceptionHandler.handleException(e);
            System.exit(exitCode);
        } catch (Error e) {
            int exitCode = ExceptionHandler.handleError(e);
            System.exit(exitCode);
        }
    }
}
