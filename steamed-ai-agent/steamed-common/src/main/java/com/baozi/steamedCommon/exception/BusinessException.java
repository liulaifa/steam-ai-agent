package com.baozi.steamedCommon.exception;


/**
 *          业务异常类
 * 用于处理业务逻辑错误，如账号密码错误、数据不存在等
 */
public class BusinessException extends RuntimeException{

    //有参构造器
    public BusinessException(String message) {
        super(message);
    }
}
