package com.baozi.steamedCartService.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("cart")
@Tag(name = "购物车")
public class Cart {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "购物车id")
    private Long id;

    @TableField("cashier_id")
    @Schema(description = "前台id")
    private Long cashierId;

    @TableField("dish_id")
    @Schema(description = "菜品id")
    private Long dishId;

    @TableField("dish_name")
    @Schema(description = "菜品名称")
    private String dishName;

    @TableField("flavor_id")
    @Schema(description = "口味id")
    private Long flavorId;

    @TableField(value = "has_flavor",fill = FieldFill.INSERT)
    @Schema(description = "是否有口味选择，1：有 0：没有（默认）")
    private Integer hasFlavor;

    @TableField("price")
    @Schema(description = "单价（元）")
    private Integer price;

    @TableField("number")
    @Schema(description = "菜品数量")
    private Integer number;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}