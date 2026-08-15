package com.baozi.steamedCommon.domian.dto;

import lombok.Data;

@Data
public class LogPageDTO {
    private Integer page = 1;
    private Integer pageSize = 10;
}