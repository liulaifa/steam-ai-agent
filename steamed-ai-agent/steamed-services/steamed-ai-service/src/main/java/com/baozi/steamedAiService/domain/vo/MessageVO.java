package com.baozi.steamedAiService.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.Message;

@Data
@NoArgsConstructor
@Tag(name = "消息VO")
public class MessageVO {
    @Schema(description = "消息角色")
    private String role;
    @Schema(description = "消息内容")
    private String content;
    public MessageVO(Message message) {
        this.role = message.getMessageType().name().toLowerCase();
        this.content = message.getText();
    }
}
