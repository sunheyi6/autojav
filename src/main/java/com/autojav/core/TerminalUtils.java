package com.autojav.core;

public class TerminalUtils {

    // ANSI颜色代码
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    // 背景颜色
    public static final String BLACK_BG = "\u001B[40m";
    public static final String RED_BG = "\u001B[41m";
    public static final String GREEN_BG = "\u001B[42m";
    public static final String YELLOW_BG = "\u001B[43m";
    public static final String BLUE_BG = "\u001B[44m";
    public static final String PURPLE_BG = "\u001B[45m";
    public static final String CYAN_BG = "\u001B[46m";
    public static final String WHITE_BG = "\u001B[47m";

    // 样式
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";
    public static final String ITALIC = "\u001B[3m";

    /**
     * 打印彩色文本
     * @param text 文本内容
     * @param color 颜色代码
     */
    public static void printColored(String text, String color) {
        System.out.print(color + text + RESET);
    }

    /**
     * 打印彩色文本并换行
     * @param text 文本内容
     * @param color 颜色代码
     */
    public static void printlnColored(String text, String color) {
        System.out.println(color + text + RESET);
    }

    /**
     * 打印成功消息
     * @param message 消息内容
     */
    public static void printSuccess(String message) {
        printlnColored(message, GREEN);
    }

    /**
     * 打印错误消息
     * @param message 消息内容
     */
    public static void printError(String message) {
        printlnColored(message, RED);
    }

    /**
     * 打印警告消息
     * @param message 消息内容
     */
    public static void printWarning(String message) {
        printlnColored(message, YELLOW);
    }

    /**
     * 打印信息消息
     * @param message 消息内容
     */
    public static void printInfo(String message) {
        printlnColored(message, BLUE);
    }

    /**
     * 打印强调消息
     * @param message 消息内容
     */
    public static void printBold(String message) {
        printlnColored(message, BOLD);
    }

    /**
     * 检查终端是否支持ANSI颜色
     * @return 是否支持ANSI颜色
     */
    public static boolean isAnsiSupported() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Windows 10及以上版本支持ANSI颜色
            String osVersion = System.getProperty("os.version");
            try {
                double version = Double.parseDouble(osVersion);
                return version >= 10.0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        // 非Windows系统默认支持
        return true;
    }
}
