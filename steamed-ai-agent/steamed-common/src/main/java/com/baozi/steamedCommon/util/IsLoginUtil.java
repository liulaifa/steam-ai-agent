package com.baozi.steamedCommon.util;

import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.context.CashierContext;
import com.baozi.steamedCommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IsLoginUtil {
    public static void isLogin() {
        Long cashierId = CashierContext.getCurrentId();
        if (cashierId == null) {
            log.error("微服务内部二次校验，cashierId为空，用户未登录或身份信息丢失");
            throw new BusinessException(MessageConstant.NOT_LOGIN);//未登录
        }
    }
    public static void isLogin(Long cashierId) {
        if (cashierId==null) {
            cashierId = CashierContext.getCurrentId();
            if (cashierId == null) {
                log.error("微服务内部二次校验，cashierId为空，用户未登录或身份信息丢失");
                throw new BusinessException(MessageConstant.NOT_LOGIN);//未登录
            }
        }
    }
}
