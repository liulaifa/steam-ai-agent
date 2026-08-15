package com.baozi.steamedAiService.controller;

import com.baozi.steamedAiService.service.IChatHistoryService;
import com.baozi.steamedAiService.domain.vo.MessageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chatHistory")
@Tag(name = "会话记录接口")
public class ChatHistoryController {

    private final IChatHistoryService chatHistoryService;

    /**
     * 根据用户id获取用户聊天记录
     */
    @Operation(summary = "根据用户id获取用户聊天记录")
    @GetMapping("/list")
    public List<MessageVO> list(@RequestHeader("X-User-Id") String xUserId){
        return chatHistoryService.list(xUserId);
    }


    /**
     * 清空用户聊天记录
     */
    @Operation(summary = "清空用户聊天记录")
    @DeleteMapping("/delete")
    public void delete(@RequestHeader("X-User-Id") String xUserId){
        chatHistoryService.clear(xUserId);
    }

}
