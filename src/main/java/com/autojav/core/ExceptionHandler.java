package com.autojav.core;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandler {

    /**
     * 处理异常
     * @param e 异常对象
     * @return 退出码
     */
    public static int handleException(Exception e) {
        log.error("发生异常", e);
        
        // 根据异常类型进行不同处理
        if (e instanceof IllegalArgumentException) {
            TerminalUtils.printError("参数错误: " + e.getMessage());
            return 1;
        } else if (e instanceof java.io.IOException) {
            TerminalUtils.printError("IO错误: " + e.getMessage());
            return 2;
        } else if (e instanceof java.lang.reflect.InvocationTargetException) {
            Throwable targetException = ((java.lang.reflect.InvocationTargetException) e).getTargetException();
            TerminalUtils.printError("执行错误: " + targetException.getMessage());
            return 3;
        } else {
            TerminalUtils.printError("未知错误: " + e.getMessage());
            return 99;
        }
    }

    /**
     * 处理运行时异常
     * @param e 运行时异常对象
     * @return 退出码
     */
    public static int handleRuntimeException(RuntimeException e) {
        log.error("发生运行时异常", e);
        TerminalUtils.printError("运行时错误: " + e.getMessage());
        return 4;
    }

    /**
     * 处理错误
     * @param e 错误对象
     * @return 退出码
     */
    public static int handleError(Error e) {
        log.error("发生严重错误", e);
        TerminalUtils.printError("严重错误: " + e.getMessage());
        return 999;
    }

    /**
     * 打印异常堆栈信息（用于调试）
     * @param e 异常对象
     */
    public static void printStackTrace(Exception e) {
        e.printStackTrace();
    }
}