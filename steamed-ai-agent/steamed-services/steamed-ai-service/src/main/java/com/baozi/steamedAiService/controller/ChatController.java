package com.baozi.steamedAiService.controller;

import com.baozi.steamedAiService.service.IChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Tag(name = "会话接口")
public class ChatController {

    private final IChatService chatClientService;


    /**
     * 根据用户id发起AI会话
     */
    @Operation(summary = "根据用户id发起AI会话")
    @PostMapping(value = "/chat",produces = "text/html;charset = utf-8")
    public String chat(
            @RequestParam("prompt") String prompt,
            @RequestHeader("X-User-Id") String xUserId
    ){
        return chatClientService.chat(prompt,xUserId);
    }

}
