package com.autojav.core.license;

import java.util.HashMap;
import java.util.Map;

public class FeaturePermissions {

    private Map<String, Boolean> permissions;

    public FeaturePermissions() {
        permissions = new HashMap<>();
        // 初始化默认权限
        initDefaultPermissions();
    }

    /**
     * 根据版本类型初始化权限
     * @param versionType 版本类型
     */
    public FeaturePermissions(VersionType versionType) {
        permissions = new HashMap<>();
        initDefaultPermissions();
        initVersionPermissions(versionType);
    }

    /**
     * 初始化默认权限
     */
    private void initDefaultPermissions() {
        // 基础功能
        permissions.put("code.audit", true); // 代码审计
        permissions.put("doc.generate", true); // 文档生成
        permissions.put("config.manage", true); // 配置管理

        // 高级功能
        permissions.put("code.fix", false); // 代码修复
        permissions.put("ai.audit", false); // AI审计
        permissions.put("custom.template", false); // 自定义模板
        permissions.put("team.collab", false); // 团队协作
        permissions.put("private.deploy", false); // 私有化部署
        permissions.put("ci.cd.integration", false); // CI/CD集成
    }

    /**
     * 根据版本类型初始化权限
     * @param versionType 版本类型
     */
    private void initVersionPermissions(VersionType versionType) {
        switch (versionType) {
            case FREE:
                // 免费版：仅基础功能
                break;
            case TEAM:
                // 团队版：基础功能 + 部分高级功能
                permissions.put("code.fix", true);
                permissions.put("ai.audit", true);
                permissions.put("team.collab", true);
                break;
            case ENTERPRISE:
                // 企业版：所有功能
                permissions.put("code.fix", true);
                permissions.put("ai.audit", true);
                permissions.put("custom.template", true);
                permissions.put("team.collab", true);
                permissions.put("private.deploy", true);
                permissions.put("ci.cd.integration", true);
                break;
            case PERPETUAL:
                // 买断版：所有功能，永久使用
                permissions.put("code.fix", true);
                permissions.put("ai.audit", true);
                permissions.put("custom.template", true);
                permissions.put("team.collab", true);
                permissions.put("private.deploy", true);
                permissions.put("ci.cd.integration", true);
                break;
        }
    }

    /**
     * 检查是否有特定功能的权限
     * @param feature 功能名称
     * @return 是否有权限
     */
    public boolean hasPermission(String feature) {
        return permissions.getOrDefault(feature, false);
    }

    /**
     * 设置功能权限
     * @param feature 功能名称
     * @param allowed 是否允许
     */
    public void setPermission(String feature, boolean allowed) {
        permissions.put(feature, allowed);
    }

    /**
     * 获取所有权限
     * @return 权限映射
     */
    public Map<String, Boolean> getAllPermissions() {
        return permissions;
    }

    /**
     * 判断功能是否为免费功能
     * @param feature 功能名称
     * @return 是否为免费功能
     */
    public static boolean isFreeFeature(String feature) {
        return "code.audit".equals(feature) || 
               "doc.generate".equals(feature) || 
               "config.manage".equals(feature);
    }
}
