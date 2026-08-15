package com.baozi.steamedCommon.domian.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Tag(name = "日志列表VO")
public class LogListVO {
    @Schema(description = "日志ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "操作人ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long operatorId;
    @Schema(description = "操作人姓名")
    private String operatorName;
    @Schema(description = "操作内容")
    private String content;
    @Schema(description = "操作时间")
    private String createTime;
}
