package com.baozi.steamedCommon.domian.vo;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
@Tag(name = "订单详情")
public class OrderVO {
    @Schema(description = "订单id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    @Schema(description = "订单流水号")
    private String orderNumber;
    @Schema(description = "就餐方式：1堂食 2打包")
    private Integer dineType;
    @Schema(description = "总金额 单位元")
    private Integer price;
    @Schema(description = "订单状态：0草稿 1待支付 2已支付 3制作中 4已完成 5已取消")
    private Integer status;
    @Schema(description = "支付方式：1微信 2现金 3支付宝")
    private Integer payMethod;
    @Schema(description = "支付时间")
    private String payTime;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "创建时间")
    private String createTime;
    @Schema(description = "订单详情")
    private List<OrderDetailItemVO> items;
}
