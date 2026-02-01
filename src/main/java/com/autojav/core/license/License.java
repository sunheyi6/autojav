package com.autojav.core.license;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class License {

    /**
     * 许可证ID
     */
    private String licenseId;

    /**
     * 版本类型
     */
    private VersionType versionType;

    /**
     * 生效时间
     */
    private LocalDateTime startTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 功能权限
     */
    private FeaturePermissions permissions;

    /**
     * 许可证状态
     */
    private LicenseStatus status;

    /**
     * 检查许可证是否有效
     * @return 是否有效
     */
    public boolean isValid() {
        if (status != LicenseStatus.ACTIVE) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(expireTime);
    }

    /**
     * 检查是否有特定功能的权限
     * @param feature 功能名称
     * @return 是否有权限
     */
    public boolean hasPermission(String feature) {
        if (!isValid()) {
            return false;
        }
        return permissions.hasPermission(feature);
    }

    /**
     * 许可证状态枚举
     */
    public enum LicenseStatus {
        ACTIVE("激活"),
        EXPIRED("过期"),
        REVOKED("已撤销"),
        INVALID("无效");

        private final String name;

        LicenseStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}
