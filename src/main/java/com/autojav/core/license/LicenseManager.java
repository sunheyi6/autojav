package com.autojav.core.license;

import com.autojav.core.ConfigManager;
import com.autojav.core.TerminalUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class LicenseManager {

    private static final String LICENSE_FILE = ".autojav.license";
    private static final String LICENSE_KEY = "license.key";

    private License currentLicense;

    public LicenseManager() {
        loadLicense();
    }

    /**
     * 加载许可证
     */
    public void loadLicense() {
        try {
            // 从配置中读取许可证信息
            ConfigManager configManager = new ConfigManager();
            String licenseId = configManager.get(LICENSE_KEY);

            if (licenseId != null) {
                // 这里简化处理，实际应该从许可证文件或服务器验证
                // 暂时创建一个默认的免费版许可证
                currentLicense = createDefaultLicense();
                log.info("许可证加载成功: {}", currentLicense.getVersionType().getName());
            } else {
                // 没有许可证，使用免费版
                currentLicense = createDefaultLicense();
                log.info("未找到许可证，使用免费版");
            }
        } catch (Exception e) {
            log.error("加载许可证失败", e);
            // 加载失败，使用免费版
            currentLicense = createDefaultLicense();
        }
    }

    /**
     * 创建默认的免费版许可证
     * @return 免费版许可证
     */
    private License createDefaultLicense() {
        License license = new License();
        license.setLicenseId("free-" + System.currentTimeMillis());
        license.setVersionType(VersionType.FREE);
        license.setStartTime(LocalDateTime.now());
        license.setExpireTime(LocalDateTime.now().plusYears(1)); // 免费版有效期1年
        license.setPermissions(new FeaturePermissions(VersionType.FREE));
        license.setStatus(License.LicenseStatus.ACTIVE);
        return license;
    }

    /**
     * 验证许可证
     * @return 是否有效
     */
    public boolean validateLicense() {
        if (currentLicense == null) {
            return false;
        }
        return currentLicense.isValid();
    }

    /**
     * 检查是否有特定功能的权限
     * @param feature 功能名称
     * @return 是否有权限
     */
    public boolean hasPermission(String feature) {
        if (currentLicense == null) {
            return false;
        }
        return currentLicense.hasPermission(feature);
    }

    /**
     * 获取当前许可证
     * @return 当前许可证
     */
    public License getCurrentLicense() {
        return currentLicense;
    }

    /**
     * 激活许可证
     * @param licenseKey 许可证密钥
     * @return 是否激活成功
     */
    public boolean activateLicense(String licenseKey) {
        // 这里简化处理，实际应该调用服务器验证
        try {
            // 暂时创建一个团队版许可证
            License license = new License();
            license.setLicenseId(licenseKey);
            license.setVersionType(VersionType.TEAM);
            license.setStartTime(LocalDateTime.now());
            license.setExpireTime(LocalDateTime.now().plusYears(1));
            license.setPermissions(new FeaturePermissions(VersionType.TEAM));
            license.setStatus(License.LicenseStatus.ACTIVE);

            // 保存许可证信息
            ConfigManager configManager = new ConfigManager();
            configManager.set(LICENSE_KEY, licenseKey);
            configManager.save(false);

            currentLicense = license;
            TerminalUtils.printSuccess("许可证激活成功: " + license.getVersionType().getName());
            return true;
        } catch (Exception e) {
            log.error("激活许可证失败", e);
            TerminalUtils.printError("激活许可证失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 显示许可证信息
     */
    public void showLicenseInfo() {
        if (currentLicense == null) {
            TerminalUtils.printError("未找到许可证");
            return;
        }

        TerminalUtils.printInfo("许可证信息:");
        TerminalUtils.printInfo("版本: " + currentLicense.getVersionType().getName());
        TerminalUtils.printInfo("状态: " + currentLicense.getStatus().getName());
        TerminalUtils.printInfo("有效期: " + currentLicense.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " 至 " + 
                currentLicense.getExpireTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        TerminalUtils.printInfo("权限:");
        currentLicense.getPermissions().getAllPermissions().forEach((feature, allowed) -> {
            if (allowed) {
                TerminalUtils.printSuccess("  - " + feature + " (启用)");
            } else {
                TerminalUtils.printWarning("  - " + feature + " (禁用)");
            }
        });
    }

    /**
     * 检查并应用版本限制
     * @param feature 功能名称
     * @return 是否通过检查
     */
    public boolean checkAndApplyRestriction(String feature) {
        return true;
        // boolean hasPermission = hasPermission(feature);
        // if (!hasPermission) {
        //     TerminalUtils.printWarning("该功能仅在" + getRequiredVersion(feature) + "及以上可用");
        //     TerminalUtils.printInfo("当前版本: " + currentLicense.getVersionType().getName());
        //     TerminalUtils.printInfo("请升级到更高版本以使用此功能");
        // }
        // return hasPermission;
    }

    /**
     * 获取功能所需的最低版本
     * @param feature 功能名称
     * @return 所需版本
     */
    private String getRequiredVersion(String feature) {
        switch (feature) {
            case "code.fix":
            case "ai.audit":
            case "team.collab":
                return "团队版";
            case "custom.template":
            case "private.deploy":
            case "ci.cd.integration":
                return "企业版";
            default:
                return "免费版";
        }
    }
}
