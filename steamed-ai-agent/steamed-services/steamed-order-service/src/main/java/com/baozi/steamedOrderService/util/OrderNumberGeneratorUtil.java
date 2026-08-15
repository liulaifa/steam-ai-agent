package com.baozi.steamedOrderService.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

//订单号生成工具
public class OrderNumberGeneratorUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    /**
     * 生成订单号：yyyyMMddHHmmss + 3位随机数
     */
    public static String generate() {
        String dateTime = LocalDateTime.now().format(FORMATTER);
        String random = String.format("%03d", RANDOM.nextInt(1000));
        return dateTime + random;
    }
}
