package com.baozi.steamedAiService.service;

public interface IChatService {


    /**
     * 根据用户id发起AI会话
     */
    String chat(String prompt,String xUserId);
}
