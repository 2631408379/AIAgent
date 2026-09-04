package com.dongyu.superaiiagent.rag;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;


@Configuration
//初始化向量库并保存文档
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyTokenTextSplitter myTokenTextSplitter;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;

    @Bean
    public VectorStore loveAppVectorStore(EmbeddingModel dashscopeEmbeddingModel) {
        SimpleVectorStore simpleVectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();

        //加载文档
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();

        //自主切分
//        List<Document> splitDocument= myTokenTextSplitter.splitCustomized(documents);

        //自动补充元数据
        List<Document> enrichedDocuments = myKeywordEnricher.enrichDocuments(documents);
        //将文档加入到内存向量中
        simpleVectorStore.add(enrichedDocuments);
        return  simpleVectorStore;
    }

}
