package com.baozi.steamedCommon.domian.dto;

import lombok.Data;

@Data
public class OrderExportDTO {
    private String startDate;
    private String endDate;
    private Integer status;
    private Integer dineType;
}
