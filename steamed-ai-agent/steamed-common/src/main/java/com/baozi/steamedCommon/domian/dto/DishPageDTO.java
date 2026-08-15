package com.baozi.steamedCommon.domian.dto;


import lombok.Data;

@Data
public class DishPageDTO {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String name;
    private Integer status;
}
