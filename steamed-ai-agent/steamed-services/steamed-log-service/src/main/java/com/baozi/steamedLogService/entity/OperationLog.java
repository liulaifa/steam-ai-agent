package com.baozi.steamedLogService.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
@Tag(name = "操作日志")
public class OperationLog {
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "操作日志id")
    private Long id;

    @TableField("operator_id")
    @Schema(description = "操作人id")
    private Long operatorId;

    @TableField("content")
    @Schema(description = "操作内容描述")
    private String content;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}