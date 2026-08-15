package com.baozi.steamedAiService.service;

import com.baozi.steamedAiService.domain.vo.MessageVO;

import java.util.List;

public interface IChatHistoryService {

    /**
     * 根据用户id获取用户聊天记录
     */
    List<MessageVO> list(String xUserId);

    /**
     * 清空用户聊天记录
     */
    void clear(String xUserId);
}
