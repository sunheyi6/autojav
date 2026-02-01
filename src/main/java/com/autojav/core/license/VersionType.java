package com.autojav.core.license;

public enum VersionType {

    FREE("免费版", "基础功能，有使用限制"),
    TEAM("团队版", "团队协作功能"),
    ENTERPRISE("企业版", "企业级功能，支持私有化部署"),
    PERPETUAL("买断版", "永久使用，包含所有功能");

    private final String name;
    private final String description;

    VersionType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
