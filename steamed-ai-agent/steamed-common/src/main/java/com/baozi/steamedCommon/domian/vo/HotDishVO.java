package com.baozi.steamedCommon.domian.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HotDishVO {
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    private String name;
    private Integer price;
    private String img;
    private String description;
    private Integer hasFlavor;
    private Integer salesCount;  // 销量
}
