package com.baozi.steamedDishService.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("dish")
@Data
@Tag(name = "菜品")
public class Dish {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "菜品id")
    private Long id;

    @TableField("name")
    @Schema(description = "菜品名称")
    private String name;

    @TableField("category_id")
    @Schema(description = "菜品分类id")
    private Long categoryId;

    @TableField("price")
    @Schema(description = "价格 元")
    private Integer price;

    @TableField("img")
    @Schema(description = "图片路径")
    private String img;

    @TableField("description")
    @Schema(description = "菜品描述")
    private String description;

    @TableField(value = "has_flavor", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "是否有口味选择，1：有 2：没有（默认）")
    private Integer hasFlavor;

    @TableField(value = "status", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "状态：1上架 0下架(默认)")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @TableField(value = "deleted", fill = FieldFill.INSERT)
    @Schema(description = "是否删除")
    @TableLogic
    private Integer deleted;

}
