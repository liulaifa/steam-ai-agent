package com.baozi.steamedAiService.domain.entity;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.ai.tool.annotation.ToolParam;

@Data
@Tag(name = "ai模块将用户词中的信息提取成创建订单的实体类")
public class AIOrderAdd {
    @ToolParam(description = "就餐方式 1堂食 2打包")
    private Integer dineType;
    @ToolParam(required = false , description = "备注")
    private String remark;
}
