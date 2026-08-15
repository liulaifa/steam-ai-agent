package com.baozi.steamedOrderService.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class OrderDetailExcel {

    @ExcelProperty(value = "订单号", index = 0)
    @ColumnWidth(20)
    private String orderNumber;

    @ExcelProperty(value = "菜品名称", index = 1)
    @ColumnWidth(15)
    private String dishName;

    @ExcelProperty(value = "口味", index = 2)
    @ColumnWidth(25)
    private String flavorText;

    @ExcelProperty(value = "单价(元)", index = 3)
    @ColumnWidth(12)
    private Integer price;

    @ExcelProperty(value = "数量", index = 4)
    @ColumnWidth(10)
    private Integer number;

    @ExcelProperty(value = "小计(元)", index = 5)
    @ColumnWidth(12)
    private Integer subtotal;
}