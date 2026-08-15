package com.baozi.steamedCommon.domian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Data
@Tag(name = "DishUpdateDTO")
public class DishUpdateDTO {
    @Schema(description = "名称")
    private String name;
    @Schema(description = "分类id")
    private Long categoryId;
    @Schema(description = "价格 元")
    private Integer price;
    @Schema(description = "图片")
    private String img;
    @Schema(description = "描述")
    private String description;
    @Schema(description = "是否有口味")
    private Integer hasFlavor;
    @Schema(description = "状态")
    private Integer status;
}
