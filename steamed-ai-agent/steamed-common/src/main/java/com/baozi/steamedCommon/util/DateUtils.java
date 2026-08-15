package com.baozi.steamedCommon.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 日期工具类
 */
public class DateUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 格式化 LocalDateTime 为 yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    /**
     * 将日期字符串转换为当天 00:00:00 的 LocalDateTime
     */
    public static LocalDateTime parseStartOfDay(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER).atStartOfDay();
    }

    /**
     * 将日期字符串转换为当天 23:59:59 的 LocalDateTime
     */
    public static LocalDateTime parseEndOfDay(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER).atTime(23, 59, 59);
    }
}
