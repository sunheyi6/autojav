package com.autojav.core.license;

import com.autojav.core.ConfigManager;
import com.autojav.core.TerminalUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * 许可证管理器
 * 管理许可证的加载、验证、激活和权限检查
 */
@Slf4j
public class LicenseManager {

    private static final String LICENSE_KEY_CONFIG = "license.key";
    private static final String TRIAL_USED_CONFIG = "license.trial.used";
    private static final String TRIAL_START_CONFIG = "license.trial.start";

    private License currentLicense;
    private final ConfigManager configManager;

    public LicenseManager() {
        this.configManager = new ConfigManager();
        loadLicense();
    }

    /**
     * 加载许可证
     * 优先顺序：正式许可证 > 试用许可证 > 免费版
     */
    public void loadLicense() {
        try {
            // 1. 尝试加载正式许可证
            String licenseKey = configManager.get(LICENSE_KEY_CONFIG);
            if (licenseKey != null && !licenseKey.isEmpty()) {
                LicenseValidator.ValidationResult result = LicenseValidator.validate(licenseKey);
                if (result.isValid()) {
                    currentLicense = result.getLicense();
                    log.info("已加载正式许可证: {}", currentLicense.getVersionType().getName());
                    return;
                } else {
                    log.warn("许可证验证失败: {}", result.getMessage());
                }
            }

            // 2. 尝试加载试用许可证
            if (loadTrialLicense()) {
                return;
            }

            // 3. 使用免费版
            currentLicense = createFreeLicense();
            log.info("未找到有效许可证，使用免费版");

        } catch (Exception e) {
            log.error("加载许可证失败", e);
            currentLicense = createFreeLicense();
        }
    }

    /**
     * 加载试用许可证
     * @return 是否成功加载试用许可
     */
    private boolean loadTrialLicense() {
        String trialUsed = configManager.get(TRIAL_USED_CONFIG);
        if (!"true".equals(trialUsed)) {
            // 首次使用，创建试用许可证
            return startTrial();
        }

        // 检查试用是否过期
        String trialStartStr = configManager.get(TRIAL_START_CONFIG);
        if (trialStartStr != null) {
            try {
                LocalDateTime trialStart = LocalDateTime.parse(trialStartStr);
                LocalDateTime trialEnd = trialStart.plusDays(7);
                
                if (LocalDateTime.now().isBefore(trialEnd)) {
                    // 试用期内
                    currentLicense = createTrialLicense(trialStart, trialEnd);
                    long daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(), trialEnd);
                    log.info("试用许可证有效，剩余 {} 天", daysLeft);
                    return true;
                } else {
                    log.info("试用期已过期");
                }
            } catch (Exception e) {
                log.warn("解析试用时间失败", e);
            }
        }
        return false;
    }

    /**
     * 开始试用
     * @return 是否成功
     */
    private boolean startTrial() {
        try {
            LocalDateTime now = LocalDateTime.now();
            configManager.set(TRIAL_USED_CONFIG, "true");
            configManager.set(TRIAL_START_CONFIG, now.toString());
            configManager.save(false);

            LocalDateTime trialEnd = now.plusDays(7);
            currentLicense = createTrialLicense(now, trialEnd);
            
            TerminalUtils.printSuccess("🎉 已开启 7 天团队版试用！");
            log.info("试用许可证创建成功");
            return true;
        } catch (Exception e) {
            log.error("创建试用许可证失败", e);
            return false;
        }
    }

    /**
     * 创建免费版许可证
     */
    private License createFreeLicense() {
        License license = new License();
        license.setLicenseId("FREE-" + System.currentTimeMillis());
        license.setVersionType(VersionType.FREE);
        license.setStartTime(LocalDateTime.now());
        license.setExpireTime(LocalDateTime.now().plusYears(100)); // 免费版长期有效
        license.setPermissions(new FeaturePermissions(VersionType.FREE));
        license.setStatus(License.LicenseStatus.ACTIVE);
        return license;
    }

    /**
     * 创建试用版许可证
     */
    private License createTrialLicense(LocalDateTime start, LocalDateTime end) {
        License license = new License();
        license.setLicenseId("TRIAL-" + LicenseValidator.getMachineFingerprint());
        license.setVersionType(VersionType.TEAM); // 试用期间享受团队版功能
        license.setStartTime(start);
        license.setExpireTime(end);
        license.setPermissions(new FeaturePermissions(VersionType.TEAM));
        license.setStatus(License.LicenseStatus.TRIAL);
        license.setTrial(true);
        return license;
    }

    /**
     * 验证当前许可证是否有效
     * @return 是否有效
     */
    public boolean validateLicense() {
        if (currentLicense == null) {
            return false;
        }
        return currentLicense.isValid() && 
               LocalDateTime.now().isBefore(currentLicense.getExpireTime());
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
        // 如果许可证已过期，只有免费版权限
        if (LocalDateTime.now().isAfter(currentLicense.getExpireTime())) {
            return FeaturePermissions.isFreeFeature(feature);
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
     * 激活正式许可证
     * @param licenseKey 许可证密钥
     * @return 是否激活成功
     */
    public boolean activateLicense(String licenseKey) {
        try {
            // 验证密钥
            LicenseValidator.ValidationResult result = LicenseValidator.validate(licenseKey);
            
            if (!result.isValid()) {
                TerminalUtils.printError("许可证验证失败: " + result.getMessage());
                return false;
            }

            License license = result.getLicense();
            
            // 不能从正式版降级到免费版
            if (license.getVersionType() == VersionType.FREE && 
                currentLicense.getVersionType() != VersionType.FREE) {
                TerminalUtils.printWarning("无法激活免费版许可证，您当前已有更高级别许可");
                return false;
            }

            // 保存许可证
            configManager.set(LICENSE_KEY_CONFIG, licenseKey);
            configManager.save(false);

            // 更新当前许可证
            currentLicense = license;
            
            TerminalUtils.printSuccess("✅ 许可证激活成功！");
            TerminalUtils.printInfo("版本: " + license.getVersionType().getName());
            TerminalUtils.printInfo("有效期至: " + license.getExpireTime().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            
            log.info("许可证激活成功: {}", license.getVersionType().getName());
            return true;
            
        } catch (Exception e) {
            log.error("激活许可证失败", e);
            TerminalUtils.printError("激活失败: " + e.getMessage());
            return false;
        }
    }

    /**
     * 显示许可证信息
     */
    public void showLicenseInfo() {
        if (currentLicense == null) {
            TerminalUtils.printError("未找到许可证信息");
            return;
        }

        TerminalUtils.printInfo("═════════════════════════════════");
        TerminalUtils.printInfo("📋 许可证信息");
        TerminalUtils.printInfo("═════════════════════════════════");
        
        // 版本信息
        String versionStr = currentLicense.getVersionType().getName();
        if (currentLicense.isTrial()) {
            versionStr += " (试用)";
        }
        TerminalUtils.printInfo("版本类型: " + versionStr);
        
        // 状态
        String statusStr = currentLicense.getStatus().getName();
        if (!validateLicense()) {
            statusStr += " (已过期)";
        }
        TerminalUtils.printInfo("状态: " + statusStr);
        
        // 有效期
        TerminalUtils.printInfo("有效期: " + 
                currentLicense.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + 
                " 至 " + 
                currentLicense.getExpireTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        // 剩余天数
        long daysLeft = ChronoUnit.DAYS.between(LocalDateTime.now(), currentLicense.getExpireTime());
        if (daysLeft < 0) {
            TerminalUtils.printWarning("⚠️  许可证已过期 " + Math.abs(daysLeft) + " 天");
        } else if (daysLeft < 7) {
            TerminalUtils.printWarning("⚠️  许可证即将过期，剩余 " + daysLeft + " 天");
        } else {
            TerminalUtils.printInfo("剩余天数: " + daysLeft + " 天");
        }
        
        // 机器指纹
        TerminalUtils.printInfo("设备指纹: " + LicenseValidator.getMachineFingerprint());
        
        TerminalUtils.printInfo("═════════════════════════════════");
        TerminalUtils.printInfo("🔐 功能权限");
        TerminalUtils.printInfo("═════════════════════════════════");
        
        currentLicense.getPermissions().getAllPermissions().forEach((feature, allowed) -> {
            String featureName = translateFeatureName(feature);
            if (allowed) {
                TerminalUtils.printSuccess("  ✓ " + featureName);
            } else {
                TerminalUtils.printWarning("  ✗ " + featureName + " (需升级)");
            }
        });
        
        // 购买引导
        if (currentLicense.getVersionType() == VersionType.FREE || 
            currentLicense.isTrial() || 
            daysLeft < 7) {
            TerminalUtils.printInfo("═════════════════════════════════");
            TerminalUtils.printInfo("💡 升级到团队版解锁全部功能：");
            TerminalUtils.printInfo("   https://autojav.vercel.app");
        }
    }

    /**
     * 检查并应用版本限制
     * @param feature 功能名称
     * @return 是否通过检查
     */
    public boolean checkAndApplyRestriction(String feature) {
        if (hasPermission(feature)) {
            return true;
        }
        
        // 没有权限，显示提示
        String featureName = translateFeatureName(feature);
        TerminalUtils.printWarning("⛔ " + featureName + " 需要 " + getRequiredVersion(feature) + " 及以上版本");
        TerminalUtils.printInfo("当前版本: " + currentLicense.getVersionType().getName());
        
        if (currentLicense.getVersionType() == VersionType.FREE) {
            TerminalUtils.printInfo("");
            TerminalUtils.printInfo("💡 您可以：");
            TerminalUtils.printInfo("   1. 开始 7 天免费试用：autojav license trial");
            TerminalUtils.printInfo("   2. 购买正式许可证：https://autojav.vercel.app");
        } else {
            TerminalUtils.printInfo("");
            TerminalUtils.printInfo("💡 请访问官网升级：https://autojav.vercel.app");
        }
        
        return false;
    }

    /**
     * 开始试用（命令行调用）
     * @return 是否成功
     */
    public boolean startTrialFromCommand() {
        String trialUsed = configManager.get(TRIAL_USED_CONFIG);
        if ("true".equals(trialUsed)) {
            TerminalUtils.printError("您已经使用过试用期");
            return false;
        }
        
        if (currentLicense.getVersionType() != VersionType.FREE) {
            TerminalUtils.printWarning("您已有正式许可证，无需试用");
            return false;
        }
        
        return startTrial();
    }

    /**
     * 获取功能所需的最低版本
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

    /**
     * 翻译功能名称
     */
    private String translateFeatureName(String feature) {
        switch (feature) {
            case "code.audit":
                return "基础代码审计";
            case "code.fix":
                return "AI 代码修复";
            case "ai.audit":
                return "AI 深度审计";
            case "doc.generate":
                return "接口文档生成";
            case "team.collab":
                return "团队协作";
            case "custom.template":
                return "自定义模板";
            case "private.deploy":
                return "私有化部署";
            case "ci.cd.integration":
                return "CI/CD 集成";
            default:
                return feature;
        }
    }
}
