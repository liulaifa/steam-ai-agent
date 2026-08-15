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
public class DishCategoryVO {
    @Schema(description = "菜品分类id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "菜品分类名")
    private String name;
}
