package com.dongyu.superaiiagent.rag;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

//自定义错误处理逻辑
public class LoveAppContextualQueryAugmenterFactory {

    public static ContextualQueryAugmenter createInstance() {
        //自定义未检索到文档时的提示词
        PromptTemplate emptyContextPromptTemplate = new PromptTemplate("""
                你应该输出下面的内容：
                抱歉，我只能回答恋爱相关的问题，别的没办法帮到您哦，
                有问题可以联系编程导航客服 https://codefather.cn
                """);

        return ContextualQueryAugmenter.builder()
                .allowEmptyContext(false)
                .emptyContextPromptTemplate(emptyContextPromptTemplate)
                .build();
    }
}




