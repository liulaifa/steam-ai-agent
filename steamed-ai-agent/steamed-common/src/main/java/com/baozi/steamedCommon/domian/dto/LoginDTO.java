package com.baozi.steamedCommon.domian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Data
@Tag(name = "收银员登录参数")
public class LoginDTO {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
}
