package com.baozi.steamedCommon.domian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

import javax.management.Descriptor;

@Data
@Builder
@Tag(name = "添加菜品至购物车")
public class CartAddDTO {

    @Schema(description = "菜品id")
    private Long dishId;
    @Schema(description = "菜品口味id")
    private Long flavorId;
}
