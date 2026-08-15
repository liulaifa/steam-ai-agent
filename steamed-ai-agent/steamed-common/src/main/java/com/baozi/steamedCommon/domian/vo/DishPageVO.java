package com.baozi.steamedCommon.domian.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Tag(name = "菜品分类VO")
public class DishPageVO {
    @Schema(description = "菜品id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "菜品名称")
    private String name;
    @Schema(description = "菜品分类名称")
    private String categoryName;
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
