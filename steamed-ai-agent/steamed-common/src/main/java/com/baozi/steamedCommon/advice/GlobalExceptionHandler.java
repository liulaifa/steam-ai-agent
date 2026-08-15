package com.baozi.steamedCommon.advice;


import com.baozi.steamedCommon.domian.vo.Result;
import com.baozi.steamedCommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessWxception(BusinessException e){
        log.error("业务异常：{}",e.getMessage());
        return  Result.error(e.getMessage());
    }

    /**
     * 处理其他所有未捕获的异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e){
        log.error("全局异常 {}", e.getMessage());
        // 开发环境可打印异常栈，生产建议日志记录
        e.printStackTrace();
        return Result.error(e.getMessage());
    }
}
