package com.baozi.steamedCommon.domian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Data
@Tag(name = "CashierUpdateDTO")
public class CashierUpdateDTO {
    @Schema(description = "用户名")
    private String username;
    @Schema(description = "密码")
    private String password;
    @Schema(description = "真实姓名")
    private String realName;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "状态")
    private Integer status;
}