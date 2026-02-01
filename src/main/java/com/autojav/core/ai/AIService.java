package com.autojav.core.ai;

import java.util.Map;

public interface AIService {

    /**
     * 发送消息到AI模型
     * @param message 消息内容
     * @return AI回复
     * @throws AIException AI异常
     */
    String sendMessage(String message) throws AIException;

    /**
     * 发送消息到AI模型，带参数
     * @param message 消息内容
     * @param params 参数
     * @return AI回复
     * @throws AIException AI异常
     */
    String sendMessage(String message, Map<String, Object> params) throws AIException;

    /**
     * 发送代码审计请求
     * @param code 代码内容
     * @param rules 审计规则
     * @return 审计结果
     * @throws AIException AI异常
     */
    String auditCode(String code, String rules) throws AIException;

    /**
     * 发送代码修复请求
     * @param code 代码内容
     * @param issues 问题描述
     * @return 修复后的代码
     * @throws AIException AI异常
     */
    String fixCode(String code, String issues) throws AIException;

    /**
     * 生成接口文档
     * @param code 代码内容
     * @param format 文档格式
     * @return 生成的文档
     * @throws AIException AI异常
     */
    String generateDoc(String code, String format) throws AIException;

    /**
     * 设置API密钥
     * @param apiKey API密钥
     */
    void setApiKey(String apiKey);

    /**
     * 获取服务名称
     * @return 服务名称
     */
    String getServiceName();
}
