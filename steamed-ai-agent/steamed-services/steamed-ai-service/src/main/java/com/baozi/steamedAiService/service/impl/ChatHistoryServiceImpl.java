package com.baozi.steamedAiService.service.impl;

import com.baozi.steamedAiService.service.IChatHistoryService;
import com.baozi.steamedAiService.domain.vo.MessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl implements IChatHistoryService {

    private final ChatMemory chatMemory;

    /**
     * 根据用户id获取用户聊天记录
     */
    @Override
    public List<MessageVO> list(String xUserId) {
        List<Message> messages = chatMemory.get(xUserId, Integer.MAX_VALUE);
        if(messages == null){
            return List.of();
        }
        return messages.stream().map(MessageVO::new).toList();
    }

    /**
     * 清空用户聊天记录
     */
    @Override
    public void clear(String xUserId) {
        chatMemory.clear(xUserId);
    }
}
