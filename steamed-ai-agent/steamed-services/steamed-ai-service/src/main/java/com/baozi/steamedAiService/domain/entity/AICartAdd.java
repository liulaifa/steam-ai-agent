package com.baozi.steamedAiService.domain.entity;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@Tag(name = "ai模块将用户词中的信息提取成添加购物车的实体类")
public class AICartAdd {
    @ToolParam(required = false,description = "菜品名称")
    private String name;
    @ToolParam(required = false,description = "甜度：1加 0不加")
    private Integer sweet;
    @ToolParam(required = false,description = "葱：1加 0不加")
    private Integer scallion;
    @ToolParam(required = false,description = "香菜：1加 0不加")
    private Integer coriander;
    @ToolParam(required = false,description = "辣度：0不辣 1微辣 2中辣 3特辣")
    private Integer spicy;
}
