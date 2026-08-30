package com.dongyu.superaiiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    @Override
    public String getName() {
        //为每个advisor提供一个唯一标识符
        return "故乡的自定义日志Advisor";
    }

    @Override
    public int getOrder() {
        //值越小，优先级越高
        return 10;
    }


    @Override
    //第一个参数是AI请求的包装对象,包含一次AI调用的所有信息；第二个参数是责任链对象,用于调用下一个advisor
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        //处理请求(前置处理)
        advisedRequest = this.before(advisedRequest);
        //调用链的下一个Advisor
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
        //处理响应(后置处理)
        this.observeAfter(advisedResponse);
        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        advisedRequest = this.before(advisedRequest);
        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
        return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, this::observeAfter);

    }

    private AdvisedRequest before(AdvisedRequest request) {
        log.info("AI Request:{}",request.userText());
        return request;
    }

    private void observeAfter(AdvisedResponse advisedResponse) {
        log.info("AI Response:{}",advisedResponse.response().getResult().getOutput().getText());
    }
}







