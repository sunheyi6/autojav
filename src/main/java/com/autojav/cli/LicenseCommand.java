package com.autojav.cli;

import com.autojav.core.TerminalUtils;
import com.autojav.core.license.LicenseManager;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "license",
        description = "许可证管理"
)
public class LicenseCommand implements Callable<Integer> {

    @CommandLine.Parameters(index = "0", description = "许可证操作: activate, info, trial")
    private String operation;

    @CommandLine.Parameters(index = "1", description = "许可证密钥", arity = "0..1")
    private String licenseKey;

    @Override
    public Integer call() throws Exception {
        LicenseManager licenseManager = new LicenseManager();

        switch (operation.toLowerCase()) {
            case "activate":
                if (licenseKey == null) {
                    TerminalUtils.printError("激活操作需要指定许可证密钥");
                    return 1;
                }
                boolean activated = licenseManager.activateLicense(licenseKey);
                return activated ? 0 : 1;
            case "info":
                licenseManager.showLicenseInfo();
                return 0;
            case "trial":
                boolean trialStarted = licenseManager.startTrialFromCommand();
                return trialStarted ? 0 : 1;
            default:
                TerminalUtils.printError("不支持的操作: " + operation);
                return 1;
        }
    }
}
