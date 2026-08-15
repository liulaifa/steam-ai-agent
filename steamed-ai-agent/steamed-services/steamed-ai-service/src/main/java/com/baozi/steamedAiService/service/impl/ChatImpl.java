package com.baozi.steamedAiService.service.impl;

import com.baozi.steamedAiService.service.IChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatImpl implements IChatService {

    private final ChatClient chatClient;

    /**
     * 根据用户id发起AI会话
     */
    @Override
    public String chat(String prompt,String xUserId) {
        return chatClient.prompt()
                .user(prompt)
                .advisors(a->a.param(AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY,xUserId))//将id和会话记录存入ChatMemory中
                .call()
                .content();
    }
}
