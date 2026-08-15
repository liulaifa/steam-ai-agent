package com.baozi.steamedLogService.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 操作日志 Excel 导出实体
 */
@Data
public class OperationLogExcel {

    @ExcelProperty(value = "序号", index = 0)
    @ColumnWidth(10)
    private Integer index;

    @ExcelProperty(value = "操作人", index = 1)
    @ColumnWidth(15)
    private String operatorName;

    @ExcelProperty(value = "操作内容", index = 2)
    @ColumnWidth(50)
    private String content;

    @ExcelProperty(value = "操作时间", index = 3)
    @ColumnWidth(20)
    private String createTime;
}
