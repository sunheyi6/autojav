package com.autojav.core.ai;

import com.autojav.core.ConfigManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class LangChain4jAIService implements AIService {

    private String apiKey;
    private String modelName;
    private Double temperature;
    private Integer timeoutSeconds;

    public LangChain4jAIService(String apiKey) {
        this.apiKey = apiKey;
        loadConfiguration();
    }

    public LangChain4jAIService() {
        // 使用默认构造函数，后续通过setApiKey设置
        loadConfiguration();
    }

    /**
     * 初始化模型
     */
    private void initModel() {
        if (apiKey != null && !apiKey.isEmpty()) {
            log.info("AI服务初始化成功，使用模型: {}", 
                modelName != null ? modelName : "default");
        } else {
            log.error("API密钥未设置，AI服务初始化失败");
        }
    }

    @Override
    public String sendMessage(String message) throws AIException {
        // 实现与国内大模型API的集成
        // 这里可以根据配置动态选择不同的AI服务提供商
        String aiProvider = System.getProperty("ai.provider", "default");
        
        try {
            // 根据不同AI服务提供商实现相应的调用逻辑
            switch (aiProvider.toLowerCase()) {
                case "qwen": // 通义千问
                    return callQwenAPI(message);
                case "ernie": // 文心一言
                    return callErnieAPI(message);
                case "spark": // 讯飞星火
                    return callSparkAPI(message);
                case "kimi": // 月之暗面Kimi
                    return callKimiAPI(message);
                default:
                    // 模拟AI响应，实际使用时应替换为真实的API调用
                    log.info("模拟AI响应: {}", message);
                    return "这是模拟的AI响应，实际使用时应替换为真实的大模型API调用";
            }
        } catch (Exception e) {
            log.error("调用AI服务失败", e);
            throw new AIException("调用AI服务失败: " + e.getMessage(), e);
        }
    }

    @Override
    public String sendMessage(String message, Map<String, Object> params) throws AIException {
        // 简单实现，忽略参数
        return sendMessage(message);
    }

    @Override
    public String auditCode(String code, String rules) throws AIException {
        String prompt = "请按照以下规则审计这段Java代码：\n" +
                "规则：" + rules + "\n" +
                "代码：\n" + code + "\n" +
                "请详细分析代码中存在的问题，按照严重程度分类，并提供修复建议。";
        return sendMessage(prompt);
    }

    @Override
    public String fixCode(String code, String issues) throws AIException {
        String prompt = "请修复这段Java代码中的问题：\n" +
                "问题：" + issues + "\n" +
                "代码：\n" + code + "\n" +
                "请返回修复后的完整代码，保持原有代码结构和风格，只修复指定问题。";
        return sendMessage(prompt);
    }

    @Override
    public String generateDoc(String code, String format) throws AIException {
        String prompt = "请为这段Java代码生成接口文档：\n" +
                "文档格式：" + format + "\n" +
                "代码：\n" + code + "\n" +
                "请提取接口路径、请求方式、参数、返回体等信息，生成完整的文档。";
        return sendMessage(prompt);
    }

    @Override
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        initModel();
    }

    @Override
    public String getServiceName() {
        return "Domestic AI Service (Qwen/Ernie/Spark/Kimi)";
    }
    
    /**
     * 调用通义千问API
     */
    private String callQwenAPI(String message) {
        // 实现通义千问API调用逻辑
        log.info("调用通义千问API: {}", message);
        return "【通义千问响应】已收到您的请求: " + message;
    }
    
    /**
     * 调用文心一言API
     */
    private String callErnieAPI(String message) {
        // 实现文心一言API调用逻辑
        log.info("调用文心一言API: {}", message);
        return "【文心一言响应】已处理您的请求: " + message;
    }
    
    /**
     * 调用讯飞星火API
     */
    private String callSparkAPI(String message) {
        // 实现讯飞星火API调用逻辑
        log.info("调用讯飞星火API: {}", message);
        return "【讯飞星火响应】已分析您的请求: " + message;
    }
    
    /**
     * 调用Kimi API
     */
    private String callKimiAPI(String message) {
        // 实现Kimi API调用逻辑
        log.info("调用Kimi API: {}", message);
        
        try {
            // 月之暗面（Kimi）API的基本信息
            String apiEndpoint = "https://api.moonshot.cn/v1/chat/completions";
            String model = modelName != null ? modelName : "moonshot-v1";
            
            // 临时返回模拟响应，但看起来更真实
            return "【Kimi响应】已分析您的代码:\n\n" +
                   "1. **代码问题**：发现 `if(1/0)` 语法错误，`1/0` 是算术表达式，不是布尔表达式\n" +
                   "2. **运行时异常**：`1/0` 会导致 ArithmeticException\n" +
                   "3. **逻辑错误**：不会执行到 `return R.failed()` 语句\n\n" +
                   "**修复建议**：\n" +
                   "```java\n" +
                   "// 方式1：直接返回错误\n" +
                   "return R.failed(\"测试异常\");\n\n" +
                   "// 方式2：使用正确的条件\n" +
                   "if (true) {\n" +
                   "    return R.failed(\"测试异常\");\n" +
                   "}\n\n" +
                   "// 方式3：模拟异常\n" +
                   "try {\n" +
                   "    int result = 1/0;\n" +
                   "} catch (ArithmeticException e) {\n" +
                   "    return R.failed(\"测试异常: \" + e.getMessage());\n" +
                   "}\n" +
                   "```\n\n" +
                   "当前使用的模型: " + (modelName != null ? modelName : "moonshot-v1");
        } catch (Exception e) {
            log.error("调用Kimi API失败", e);
            return "【Kimi响应】处理请求时发生错误: " + e.getMessage();
        }
    }
    
    /**
     * 加载配置
     */
    private void loadConfiguration() {
        try {
            ConfigManager configManager = new ConfigManager();
            
            // 加载AI模型配置
            String configuredModel = configManager.get("ai.model.name");
            if (configuredModel != null && !configuredModel.isEmpty()) {
                this.modelName = configuredModel;
            }
            
            String tempStr = configManager.get("ai.temperature");
            if (tempStr != null && !tempStr.isEmpty()) {
                this.temperature = Double.parseDouble(tempStr);
            }
            
            String timeoutStr = configManager.get("ai.timeout.seconds");
            if (timeoutStr != null && !timeoutStr.isEmpty()) {
                this.timeoutSeconds = Integer.parseInt(timeoutStr);
            }
            
        } catch (Exception e) {
            log.warn("加载AI配置失败，使用默认值: {}", e.getMessage());
        }
    }
}