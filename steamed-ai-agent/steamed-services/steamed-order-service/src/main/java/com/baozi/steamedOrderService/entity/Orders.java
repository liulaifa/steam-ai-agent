package com.baozi.steamedOrderService.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("orders")
@Tag(name = "订单")
public class Orders {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "订单id")
    private Long id;

    @TableField("order_number")
    @Schema(description = "订单流水号")
    private String orderNumber;

    @TableField("dine_type")
    @Schema(description = "就餐方式：1堂食 2打包")
    private Integer dineType;

    @TableField("price")
    @Schema(description = "总金额 单位元")
    private Integer price;

    @TableField("status")
    @Schema(description = "订单状态：0草稿 1待支付 2已支付 3制作中 4已完成 5已取消")
    private Integer status;

    @TableField("pay_method")
    @Schema(description = "支付方式：1微信 2现金 3支付宝")
    private Integer payMethod;

    @TableField("pay_time")
    @Schema(description = "支付时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime payTime;

    @TableField("cashier_id")
    @Schema(description = "收银员id")
    private Long cashierId;

    @TableField("customer_id")
    @Schema(description = "顾客id")
    private Long customerId;

    @TableField("remark")
    @Schema(description = "备注")
    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}