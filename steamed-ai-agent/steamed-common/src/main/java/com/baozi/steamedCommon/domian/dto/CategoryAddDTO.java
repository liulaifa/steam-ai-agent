package com.baozi.steamedCommon.domian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Data
@Tag(name = "菜品分类新增参数")
public class CategoryAddDTO {
    @Schema(description = "菜品分类名")
    private String name;
    @Schema(description = "状态：1起售 0停售(默认)")
    private Integer status;
}
