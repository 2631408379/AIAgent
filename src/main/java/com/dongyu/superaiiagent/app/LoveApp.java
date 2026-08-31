package com.dongyu.superaiiagent.app;

import com.dongyu.superaiiagent.advisor.MyLoggerAdvisor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

//创建一个基于内存存储对话记忆的AI应用
@Component
@Slf4j
public class LoveApp {

    //定义chatclient对象以及系统提示词
    private final ChatClient chatClient;

    private final String SYSTEM_PROMPT="扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。\n";

    //定义恋爱报告类
    public record LoveReport(String title, List<String> suggestions){};

    //初始化chatClient对象,指定系统提示词以及记忆advisor。通过构造器注入的方式注入dashscopeChatModel.
    private LoveApp(ChatModel dashscopeChatModel) {

        //初始化基于文件的对话记忆
        //定义文件的存放目录
        /*String fileDir=System.getProperty("user.dir")+"/chat-memory";

        FileBasedChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient=ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
                )
                .build();*/

        //初始化基于内存的对话记忆
        InMemoryChatMemory chatMemory = new InMemoryChatMemory();
        chatClient=ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MyLoggerAdvisor()

                )
                .build();

    }


    //编写对话方法。传入用户提示词，指定聊天的chatId
    public String doChat(String message,String chatId) {
        ChatResponse response = chatClient.prompt() //开始构建AI请求
                .user(message)
                .call()
                .chatResponse();
        String result = response.getResult().getOutput().getText();
        return result;
    }

    //生成恋爱报告的方法。只需补充原有系统提示词即，并添加结构化输出的代码

    public LoveReport doChatWithReport(String message,String chatId) {

        LoveReport loveReport = chatClient.prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))//配置聊天记忆参数(对话会话ID和检索的历史消息数量,前面是参数名，后面是参数值)
                .call()
                .entity(LoveReport.class);//按LoveReport类来输出
        log.info("loveReport:{}",loveReport);
        return loveReport;
    }

    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppRagCloudAdvisor;

    public String doChatWithRag(String message,String chatId) {
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())//开启日志
//                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))//本地知识库问答
                .advisors(loveAppRagCloudAdvisor)//云知识库问答
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}",content);
        return content;
    }

}
