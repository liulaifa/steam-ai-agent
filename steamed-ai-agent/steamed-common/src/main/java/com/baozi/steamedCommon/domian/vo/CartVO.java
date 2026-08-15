package com.baozi.steamedCommon.domian.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CartVO {

    @Schema(description = "购物车id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "前台id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cashierId;
    @Schema(description = "菜品id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long dishId;
    @Schema(description = "菜品名称")
    private String dishName;
    @Schema(description = "口味id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long flavorId;
    @Schema(description = "是否有口味选择，1：有 0：没有（默认）")
    private Integer hasFlavor;
    @Schema(description = "单价（元）")
    private Integer price;
    @Schema(description = "菜品数量")
    private Integer number;

    @Schema(description = "菜品口味")
    private DishFlavorVO dishFlavorVO;

}
