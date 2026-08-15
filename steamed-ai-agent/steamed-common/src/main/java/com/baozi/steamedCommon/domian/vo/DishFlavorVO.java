package com.baozi.steamedCommon.domian.vo;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@Tag(name = "菜品口味VO")
public class DishFlavorVO {

    @Schema(description = "口味id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "甜度")
    private Integer sweet;
    @Schema(description = "葱")
    private Integer scallion;
    @Schema(description = "香菜")
    private Integer coriander;
    @Schema(description = "辣度")
    private Integer spicy;
}
