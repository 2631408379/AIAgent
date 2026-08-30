package com.dongyu.superaiiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LoveAppDocumentLoader {

    //构造器注入资源路径解析器(因为文件在本地)
    private  final ResourcePatternResolver resourcePatternResolver;

    LoveAppDocumentLoader(ResourcePatternResolver resourcePatternResolver){
        this.resourcePatternResolver=resourcePatternResolver;
    }

    //加载文件方法
    public List<Document> loadMarkdowns(){

        //定义结果集合
        List<Document> documents = new ArrayList<>();

        //读取多个md文件

        try {

            //1.从路径解析器获取文件资源
            Resource[] resources = resourcePatternResolver.getResources("classpath:document/*.md");

            //2.遍历每个文件，并添加配置信息读取文件
            for (Resource resource : resources) {
                //提取文件名，方便整理元数据信息
                String fileName = resource.getFilename();
                MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                        .withHorizontalRuleCreateDocument(true)
                        .withIncludeCodeBlock(false)
                        .withIncludeBlockquote(false)
                        .withAdditionalMetadata("filename", fileName)
                        .build();

                MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
                //添加到结果列表中(MarkdownDocumentReader读取的是一组文档，因为一个md文件里可以包含多个#,##等分隔的子文档，reader.get返回的是一个文档列表)
                documents.addAll(reader.get());
            }
        } catch (IOException e) {
            log.error("文档读取失败，原因是：{}",e.getMessage());
        }

        return documents;


    }

}
