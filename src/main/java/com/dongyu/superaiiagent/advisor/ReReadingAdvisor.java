package com.dongyu.superaiiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.prompt.PromptTemplate;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;

//自定义二次阅读提示词的拦截器。将用户的原始问题嵌入到一个新的提示词模板中。
@Slf4j
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private AdvisedRequest before(AdvisedRequest advisedRequest) {

        // 创建包含变量的模板
        String template = """
        {re2_input_query}
        Read the question again: {re2_input_query}
        """;

        // 使用 PromptTemplate 处理模板
        PromptTemplate promptTemplate = new PromptTemplate(template);

        //获取请求的用户参数并拷贝到map中
        Map<String, Object> advisedUserParams = new HashMap<>();

        //将用户提示词添加到新map中
        advisedUserParams.put("re2_input_query",advisedRequest.userText());

        String renderedText = promptTemplate.render(advisedUserParams);

        //构建新的请求
        return AdvisedRequest.from(advisedRequest)  //基于原请求
                .userText(renderedText)    //使用渲染后的文本
                .userParams(advisedUserParams)  //设置新的参数
                .build();   //构建新请求

    }

    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

}
