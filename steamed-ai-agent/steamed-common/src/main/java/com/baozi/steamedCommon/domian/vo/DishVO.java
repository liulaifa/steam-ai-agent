package com.baozi.steamedCommon.domian.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

@Data
@Tag(name = "菜品")
public class DishVO {

    @Schema(description = "菜品id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "菜品分类id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long categoryId;
    @Schema(description = "菜品名称")
    private String name;
    @Schema(description = "菜品价格 元")
    private Integer price;
    @Schema(description = "菜品图片")
    private String img;
    @Schema(description = "菜品描述")
    private String description;
    @Schema(description = "是否有口味选择，1：有 0：没有（默认）")
    private Integer hasFlavor;
    @Schema(description = "状态：1上架 0下架")
    private Integer status;
}
