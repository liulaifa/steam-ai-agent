package com.baozi.steamedDishService.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dish_category")
@Tag(name = "菜品分类")
public class DishCategory {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "菜品分类id")
    private Long id;

    @TableField("name")
    @Schema(description = "菜品分类名")
    private String name;

    @TableField(value = "status", fill = FieldFill.INSERT)
    @Schema(description = "状态：1起售 0停售(默认)")
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
