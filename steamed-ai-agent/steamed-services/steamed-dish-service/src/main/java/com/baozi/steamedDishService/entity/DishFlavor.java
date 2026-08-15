package com.baozi.steamedDishService.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

@Data
@TableName("dish_flavor")
@Tag(name = "菜品口味")
public class DishFlavor {
    //口味表中因为口味的组合是有限的，只有32中，所以一个id对应一个口味，可以复用

    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "口味id")
    private Long id;

    @TableField(value = "sweet")
    @Schema(description = "甜度：1加 0不加")
    private Integer sweet;

    @TableField("scallion")
    @Schema(description = "葱：1加 0不加")
    private Integer scallion;

    @TableField("coriander")
    @Schema(description = "香菜：1加 0不加")
    private Integer coriander;

    @TableField("spicy")
    @Schema(description = "辣度：0不辣 1微辣 2中辣 3特辣")
    private Integer spicy;
}
