package com.baozi.steamedCommon.domian.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Tag(name = "OrderResultVO")
public class OrderResultVO {
    @Schema(description = "订单id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long orderId;
    @Schema(description = "订单编号")
    private String orderNumber;
}
