package com.autojav.core.doc;

public class DocGeneratorFactory {

    /**
     * 创建文档生成器
     * @param format 文档格式
     * @return 文档生成器实例
     * @throws DocGenerationException 文档生成异常
     */
    public static DocGenerator createDocGenerator(String format) throws DocGenerationException {
        switch (format.toLowerCase()) {
            case "markdown":
            case "md":
                return new MarkdownDocGenerator();
            case "openapi":
            case "swagger":
            case "yaml":
                return new OpenApiDocGenerator();
            case "json":
                return new OpenApiDocGenerator();
            default:
                throw new DocGenerationException("不支持的文档格式: " + format);
        }
    }
}
