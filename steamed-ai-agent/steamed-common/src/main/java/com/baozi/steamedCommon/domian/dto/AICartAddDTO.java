package com.baozi.steamedCommon.domian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AICartAddDTO {
    @Schema(description = "菜品名称")
    private String name;
    @Schema(description = "甜度：1加 0不加")
    private Integer sweet;
    @Schema(description = "葱：1加 0不加")
    private Integer scallion;
    @Schema(description = "香菜：1加 0不加")
    private Integer coriander;
    @Schema(description = "辣度：0不辣 1微辣 2中辣 3特辣")
    private Integer spicy;
}
