package com.baozi.steamedCommon.domian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Data
@Tag(name = "修改购物车内菜品数量")
public class CartUpdateDTO {

    @Schema(description = "菜品id")
    private Long dishId;
    @Schema(description = "菜品口味id")
    private Long flavorId;
    @Schema(description = "菜品数量，正数加，负数减")
    private Integer number;

}
