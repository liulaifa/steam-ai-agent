package com.baozi.steamedOrderService.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class OrderSummaryExcel {

    @ExcelProperty(value = "订单号", index = 0)
    @ColumnWidth(20)
    private String orderNumber;

    @ExcelProperty(value = "下单时间", index = 1)
    @ColumnWidth(20)
    private String createTime;

    @ExcelProperty(value = "就餐方式", index = 2)
    @ColumnWidth(12)
    private String dineType;

    @ExcelProperty(value = "订单状态", index = 3)
    @ColumnWidth(12)
    private String status;

    @ExcelProperty(value = "总金额(元)", index = 4)
    @ColumnWidth(15)
    private Integer totalPrice;

    @ExcelProperty(value = "备注", index = 5)
    @ColumnWidth(30)
    private String remark;
}
