package com.baozi.steamedCommon.domian.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Tag(name = "菜品分类")
public class CategoryVO {
    @Schema(description = "菜品分类id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "菜品分类名")
    private String name;
    @Schema(description = "状态：1起售 0停售(默认)")
    private Integer status;
}
