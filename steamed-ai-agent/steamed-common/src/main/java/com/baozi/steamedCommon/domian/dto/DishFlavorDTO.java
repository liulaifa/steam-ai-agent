package com.baozi.steamedCommon.domian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Tag(name = "菜品口味DTO")
public class DishFlavorDTO {
    @Schema(description = "甜度 1加 0不加")
    private Integer sweet;
    @Schema(description = "葱 1加 0不加")
    private Integer scallion;
    @Schema(description = "香菜 1加 0不加")
    private Integer coriander;
    @Schema(description = "辣度 0不辣 1微辣 2中辣 3特辣")
    private Integer spicy;
}
