package com.baozi.steamedCommon.domian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Data
@Tag(name = "订单添加参数")
public class OrderAddDTO {
    @Schema(description = "堂食打包 1堂食 2打包")
    private Integer dineType;
    @Schema(description = "订单详情")
    private String remark;
}