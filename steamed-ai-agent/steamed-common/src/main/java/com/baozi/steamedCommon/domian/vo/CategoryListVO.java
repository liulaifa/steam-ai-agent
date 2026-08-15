package com.baozi.steamedCommon.domian.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Tag(name = "菜品分类列表VO")
public class CategoryListVO {
    @Schema(description = "菜品分类id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;//菜品分类id
    @Schema(description = "菜品分类名")
    private String name;//菜品分类名
    @Schema(description = "状态：1起售 0停售")
    private Integer status;// 状态：1起售 2停售
    @Schema(description = "菜品总数")
    private Integer dishCount;//菜品总数
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
