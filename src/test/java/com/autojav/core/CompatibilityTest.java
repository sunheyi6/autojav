package com.autojav.core;

class CompatibilityTest {

    public static void main(String[] args) {
        // 测试Java版本兼容性
        testJavaVersion();

        // 测试操作系统兼容性
        testOperatingSystem();

        // 测试核心功能兼容性
        testCoreFunctionality();
    }

    private static void testJavaVersion() {
        String javaVersion = System.getProperty("java.version");
        String javaVendor = System.getProperty("java.vendor");
        System.out.println("Java版本: " + javaVersion);
        System.out.println("Java厂商: " + javaVendor);

        // 检查Java版本是否符合要求（至少Java 8）
        int majorVersion = getJavaMajorVersion(javaVersion);
        if (majorVersion >= 8) {
            System.out.println("Java版本兼容: 支持Java " + javaVersion);
        } else {
            System.out.println("Java版本不兼容: 需要Java 8或更高版本");
        }
    }

    private static void testOperatingSystem() {
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String osArch = System.getProperty("os.arch");
        System.out.println("操作系统: " + osName + " " + osVersion + " " + osArch);

        // 检查操作系统兼容性
        if (osName.toLowerCase().contains("windows") || 
            osName.toLowerCase().contains("linux") || 
            osName.toLowerCase().contains("mac")) {
            System.out.println("操作系统兼容: 支持" + osName);
        } else {
            System.out.println("操作系统可能不兼容: " + osName);
        }
    }

    private static void testCoreFunctionality() {
        try {
            // 测试核心类是否可以加载
            Class.forName("com.autojav.core.CodeParser");
            Class.forName("com.autojav.core.ConfigManager");
            Class.forName("com.autojav.core.TerminalUtils");
            System.out.println("核心功能兼容: 所有核心类加载成功");
        } catch (ClassNotFoundException e) {
            System.out.println("核心功能不兼容: 无法加载核心类 - " + e.getMessage());
        }
    }

    private static int getJavaMajorVersion(String javaVersion) {
        if (javaVersion.startsWith("1.")) {
            // Java 8及之前的版本格式: 1.8.0_292
            return Integer.parseInt(javaVersion.substring(2, 3));
        } else {
            // Java 9及之后的版本格式: 11.0.12
            int dotIndex = javaVersion.indexOf('.');
            if (dotIndex > 0) {
                return Integer.parseInt(javaVersion.substring(0, dotIndex));
            } else {
                return Integer.parseInt(javaVersion);
            }
        }
    }
}
