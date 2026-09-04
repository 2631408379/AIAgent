package com.dongyu.superaiiagent.rag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

@Slf4j
public class LoveAppRagCustomAdvisorFactory {
    public static Advisor createLoveAppCustomAdvisor(VectorStore vectorStore,String status) {

        //构建筛选条件
        Filter.Expression expression = new FilterExpressionBuilder().eq("status", status).build();

        //构建文件检索器对象
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .filterExpression(expression)
                .similarityThreshold(0.5)//相似度阈值
                .topK(3)//返回文档数量
                .build();


        //构建文档检索器advisor
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())
                .build();
    }
}
