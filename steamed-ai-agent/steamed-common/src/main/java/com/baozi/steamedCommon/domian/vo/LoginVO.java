package com.baozi.steamedCommon.domian.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

@Data
@Tag(name = "登录信息")
public class LoginVO {
    @Schema(description = "用户id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "用户名")
    private String realName;
}
