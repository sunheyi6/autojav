package com.autojav.core.license;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * 许可证生成工具
 * 用于后台生成正式许可证密钥
 * 
 * 使用方法:
 * java -cp autojav-cli.jar com.autojav.core.license.LicenseGenerator
 */
public class LicenseGenerator {

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║     AutoJava License Generator         ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        // 选择版本类型
        System.out.println("选择版本类型:");
        System.out.println("  1. 团队版 (TEAM)");
        System.out.println("  2. 企业版 (ENTERPRISE)");
        System.out.println("  3. 买断版 (PERPETUAL)");
        System.out.print("请输入选项 (1-3): ");
        
        int versionChoice = scanner.nextInt();
        scanner.nextLine(); // 消费换行
        
        VersionType versionType;
        switch (versionChoice) {
            case 1:
                versionType = VersionType.TEAM;
                break;
            case 2:
                versionType = VersionType.ENTERPRISE;
                break;
            case 3:
                versionType = VersionType.PERPETUAL;
                break;
            default:
                System.out.println("无效选项");
                return;
        }

        // 输入有效期
        System.out.println();
        System.out.println("选择有效期:");
        System.out.println("  1. 1年");
        System.out.println("  2. 2年");
        System.out.println("  3. 3年");
        System.out.println("  4. 永久（仅买断版）");
        System.out.println("  5. 自定义天数");
        System.out.print("请输入选项 (1-5): ");
        
        int durationChoice = scanner.nextInt();
        scanner.nextLine(); // 消费换行
        
        LocalDateTime expireTime;
        switch (durationChoice) {
            case 1:
                expireTime = LocalDateTime.now().plusYears(1);
                break;
            case 2:
                expireTime = LocalDateTime.now().plusYears(2);
                break;
            case 3:
                expireTime = LocalDateTime.now().plusYears(3);
                break;
            case 4:
                if (versionType != VersionType.PERPETUAL) {
                    System.out.println("永久有效期仅适用于买断版");
                    return;
                }
                expireTime = LocalDateTime.now().plusYears(100);
                break;
            case 5:
                System.out.print("请输入天数: ");
                int days = scanner.nextInt();
                scanner.nextLine();
                expireTime = LocalDateTime.now().plusDays(days);
                break;
            default:
                System.out.println("无效选项");
                return;
        }

        // 是否绑定机器
        System.out.println();
        System.out.print("是否绑定特定机器? (y/n): ");
        String bindChoice = scanner.nextLine().trim().toLowerCase();
        
        String machineFingerprint;
        if (bindChoice.equals("y")) {
            System.out.print("请输入机器指纹: ");
            machineFingerprint = scanner.nextLine().trim();
        } else {
            machineFingerprint = "ANY";
        }

        // 生成许可证
        System.out.println();
        System.out.println("正在生成许可证...");
        
        String licenseKey = LicenseValidator.generateLicenseKey(
                versionType, expireTime, machineFingerprint);

        // 显示结果
        System.out.println();
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║              许可证生成成功                            ║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.println("║ 版本类型: " + padRight(versionType.getName(), 41) + "║");
        System.out.println("║ 有效期至: " + padRight(expireTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 41) + "║");
        System.out.println("║ 机器绑定: " + padRight(machineFingerprint.equals("ANY") ? "不限" : machineFingerprint, 41) + "║");
        System.out.println("╠════════════════════════════════════════════════════════╣");
        System.out.println("║ 许可证密钥 (请复制给客户):                             ║");
        System.out.println("║ " + padRight(licenseKey, 52) + " ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        
        System.out.println();
        System.out.println("激活命令:");
        System.out.println("  autojav license activate " + licenseKey);
    }

    private static String padRight(String s, int n) {
        if (s.length() > n) {
            return s.substring(0, n);
        }
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < n) {
            sb.append(" ");
        }
        return sb.toString();
    }
}
