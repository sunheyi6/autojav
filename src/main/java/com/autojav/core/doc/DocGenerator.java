package com.autojav.core.doc;

import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.util.List;

public interface DocGenerator {

    /**
     * 生成文档
     * @param compilationUnits 编译单元列表
     * @return 生成的文档内容
     * @throws DocGenerationException 文档生成异常
     */
    String generate(List<CompilationUnit> compilationUnits) throws DocGenerationException;

    /**
     * 生成文档并保存到文件
     * @param compilationUnits 编译单元列表
     * @param outputFile 输出文件
     * @throws DocGenerationException 文档生成异常
     */
    void generateAndSave(List<CompilationUnit> compilationUnits, File outputFile) throws DocGenerationException;

    /**
     * 获取文档格式
     * @return 文档格式
     */
    String getFormat();
}
