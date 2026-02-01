package com.autojav.cli;

import com.autojav.core.ConfigManager;
import com.autojav.core.TerminalUtils;
import picocli.CommandLine;


import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "config",
        description = "配置管理"
)
public class ConfigCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "配置操作: set, get, list")
    private String operation;

    @CommandLine.Parameters(index = "1", description = "配置键", arity = "0..1")
    private String key;

    @CommandLine.Parameters(index = "2", description = "配置值", arity = "0..1")
    private String value;

    @CommandLine.Option(names = {"-g", "--global"}, description = "使用全局配置")
    private boolean global;

    @Override
    public Integer call() throws Exception {
        ConfigManager configManager = new ConfigManager(global);

        switch (operation.toLowerCase()) {
            case "set":
                if (key == null || value == null) {
                    TerminalUtils.printError("set操作需要指定配置键和值");
                    return 1;
                }
                configManager.set(key, value);
                configManager.save(global);
                TerminalUtils.printSuccess("配置已保存: " + key + " = " + value);
                break;
            case "get":
                if (key == null) {
                    TerminalUtils.printError("get操作需要指定配置键");
                    return 1;
                }
                String configValue = configManager.get(key);
                if (configValue != null) {
                    TerminalUtils.printInfo(key + " = " + configValue);
                } else {
                    TerminalUtils.printWarning("配置键不存在: " + key);
                }
                break;
            case "list":
                TerminalUtils.printInfo("当前配置:");
                configManager.list();
                break;
            default:
                TerminalUtils.printError("不支持的操作: " + operation);
                return 1;
        }
        return 0;
    }
}
