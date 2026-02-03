package com.autojav.core.ai;

public class AIServiceFactory {

    /**
     * 创建AI服务实例
     * @param serviceType 服务类型
     * @param apiKey API密钥
     * @return AI服务实例
     */
    public static AIService createAIService(String serviceType, String apiKey) {
        switch (serviceType.toLowerCase()) {
            case "qwen":
            case "tongyi":
            case "aliyun":
                // 针对不同服务类型可以有不同的实现
                System.setProperty("ai.provider", "qwen");
                return new LangChain4jAIService(apiKey);
            case "ernie":
            case "baidu":
                System.setProperty("ai.provider", "ernie");
                return new LangChain4jAIService(apiKey);
            case "spark":
            case "xfyun":
                System.setProperty("ai.provider", "spark");
                return new LangChain4jAIService(apiKey);
            case "kimi":
            case "moonshot":
                System.setProperty("ai.provider", "kimi");
                return new LangChain4jAIService(apiKey);
            case "openai":
            case "langchain4j":
            default:
                // 默认使用通用实现
                return new LangChain4jAIService(apiKey);
        }
    }

    /**
     * 创建AI服务实例，使用默认服务类型
     * @param apiKey API密钥
     * @return AI服务实例
     */
    public static AIService createAIService(String apiKey) {
        return createAIService("kimi", apiKey); // 默认使用Kimi
    }

    /**
     * 创建AI服务实例，后续通过setApiKey设置API密钥
     * @param serviceType 服务类型
     * @return AI服务实例
     */
    public static AIService createAIServiceWithServiceType(String serviceType) {
        switch (serviceType.toLowerCase()) {
            case "qwen":
            case "tongyi":
            case "aliyun":
                System.setProperty("ai.provider", "qwen");
                return new LangChain4jAIService();
            case "ernie":
            case "baidu":
                System.setProperty("ai.provider", "ernie");
                return new LangChain4jAIService();
            case "spark":
            case "xfyun":
                System.setProperty("ai.provider", "spark");
                return new LangChain4jAIService();
            case "kimi":
            case "moonshot":
                System.setProperty("ai.provider", "kimi");
                return new LangChain4jAIService();
            case "openai":
            case "langchain4j":
            default:
                return new LangChain4jAIService(); // 默认使用通用实现
        }
    }

    /**
     * 创建AI服务实例，使用默认服务类型，后续通过setApiKey设置API密钥
     * @return AI服务实例
     */
    public static AIService createAIService() {
        return createAIServiceWithServiceType("kimi");
    }
}