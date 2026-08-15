package com.baozi.steamedCommon.domian.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Tag(name = "订单详情项")
public class OrderDetailItemVO {
    @Schema(description = "订单Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long OrderId;
    @Schema(description = "菜品名称")
    private String dishName;
    @Schema(description = "口味id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long flavorId;
    @Schema(description = "单价")
    private Integer price;
    @Schema(description = "数量")
    private Integer number;
    @Schema(description = "口味VO")
    private DishFlavorVO dishFlavorVO;
}
