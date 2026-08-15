package com.baozi.steamedCommon.domian.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Tag(name = "订单分页查询参数")
public class OrderPageDTO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "开始时间")
    private LocalDateTime startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "结束时间")
    private LocalDateTime endDate;
    @Schema(description = "订单状态")
    private Integer status;
    @Schema(description = "页码")
    private Integer page = 1;
    @Schema(description = "每页数量")
    private Integer pageSize = 10;
}
