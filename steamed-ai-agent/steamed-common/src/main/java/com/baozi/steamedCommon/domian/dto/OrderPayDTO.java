package com.baozi.steamedCommon.domian.dto;

import lombok.Data;

@Data
public class OrderPayDTO {
    private Integer payMethod;  // 1微信 2现金 3支付宝
}
