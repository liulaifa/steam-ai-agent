package com.baozi.steamedCommon.constant;

/**
 * 订单状态常量
 */
public class OrderStatusConstant {

    /**
     * 待支付
     */
    public static final Integer PENDING_PAYMENT = 1;

    /**
     * 已支付
     */
    public static final Integer PAID = 2;

    /**
     * 制作中
     */
    public static final Integer COOKING = 3;

    /**
     * 已完成
     */
    public static final Integer COMPLETED = 4;

    /**
     * 已取消
     */
    public static final Integer CANCELLED = 5;

    /**
     * 正常出餐
     */
    public static final Integer GET_DISH = 1;

    /**
     * 退菜
     */
    public static final Integer RETURN_DISH = 0;
}
