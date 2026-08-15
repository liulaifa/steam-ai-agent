package com.baozi.steamedOrderService.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_details")
@Tag(name = "订单详情")
public class OrderDetails {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "订单详情id")
    private Long id;

    @TableField("order_id")
    @Schema(description = "订单id")
    private Long orderId;

    @TableField("dish_id")
    @Schema(description = "菜品id")
    private Long dishId;

    @TableField("dish_name")
    @Schema(description = "菜品名称")
    private String dishName;

    @TableField("flavor_id")
    @Schema(description = "口味id")
    private Long flavorId;

    @TableField("price")
    @Schema(description = "单价")
    private Integer price;

    @TableField("number")
    @Schema(description = "菜品的数量")
    private Integer number;

    @TableField("status")
    @Schema(description = "明细状态：1正常 0已退菜")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}