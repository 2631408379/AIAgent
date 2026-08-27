package com.dongyu.superaiiagent.app;

import com.dongyu.superaiiagent.advisor.MyLoggerAdvisor;
import com.dongyu.superaiiagent.advisor.ReReadingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

//创建一个基于内存存储对话记忆的AI应用
@Component
@Slf4j
public class LoveApp {

    //定义chatclient对象以及系统提示词
    private final ChatClient chatClient;

    private String system_prompt="扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。\n";


    //1.初始化chatClient对象,指定系统提示词以及记忆advisor。通过构造器注入的方式注入dashscopeChatModel.
    private LoveApp(ChatModel dashscopeChatModel) {

        //初始化基于内存的对话记忆
        InMemoryChatMemory chatMemory = new InMemoryChatMemory();
        chatClient=ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MyLoggerAdvisor()
                )
                .build();

    }


    //2.编写对话方法。传入用户提示词，指定聊天的chatId
    public String doChat(String message,String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .call()
                .chatResponse();
        String result = response.getResult().getOutput().getText();
        log.info("AI的回答是：{}",result);
        return result;
    }

    //3.测试

}
