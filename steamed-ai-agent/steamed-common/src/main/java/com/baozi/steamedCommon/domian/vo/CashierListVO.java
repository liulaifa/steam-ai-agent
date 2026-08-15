package com.baozi.steamedCommon.domian.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Tag(name = "CashierListVO")
public class CashierListVO {
    @Schema(description = "id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "账号")
    private String username;
    @Schema(description = "真实姓名")
    private String realName;
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "状态")
    private Integer status;
    @Schema(description = "最后登录时间")
    private String lastLoginTime;
    @Schema(description = "创建时间")
    private String createTime;
}
