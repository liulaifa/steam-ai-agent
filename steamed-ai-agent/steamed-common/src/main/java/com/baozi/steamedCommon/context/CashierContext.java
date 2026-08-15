package com.baozi.steamedCommon.context;

public class CashierContext{
    private static final ThreadLocal<Long> CASHIER_ID = new ThreadLocal<>();//用于存放前台的账号id

    public static void setCurrentId(Long CashierId) {
        CASHIER_ID.set(CashierId);
    }

    public static Long getCurrentId() {
        return CASHIER_ID.get();
    }

    public static void remove() {
        CASHIER_ID.remove();
    }

}
