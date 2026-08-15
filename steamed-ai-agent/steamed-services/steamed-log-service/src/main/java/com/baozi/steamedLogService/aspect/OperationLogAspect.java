package com.baozi.steamedLogService.aspect;

import com.baozi.steamedCommon.annotation.Log;
import com.baozi.steamedCommon.context.CashierContext;
import com.baozi.steamedLogService.entity.OperationLog;
import com.baozi.steamedCommon.util.SpelUtils;
import com.baozi.steamedLogService.mapper.OperationLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 操作日志切面
 */
@Aspect      // 声明这是一个切面
@Component
@Slf4j
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogMapper operationLogMapper;

    /**
     * 环绕通知：拦截带 @Log 注解的方法
     */
    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        // 1. 获取原始内容（包含 SpEL 表达式）
        String rawContent = log.value();

        // 2. 执行目标方法
        Object result = joinPoint.proceed();

        // 3. 解析 SpEL 表达式，获取最终内容
        String content = parseContent(joinPoint, rawContent);

        // 4. 保存日志
        saveLog(content);

        return result;
    }

    /**
     * 解析 SpEL 表达式
     */
    private String parseContent(ProceedingJoinPoint joinPoint, String rawContent) {
        // 如果不包含 #，说明没有 SpEL 表达式，直接返回
        if (!rawContent.contains("#")) {
            return rawContent;
        }

        try {
            // 获取目标方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // 获取方法参数值
            Object[] args = joinPoint.getArgs();

            // 解析表达式
            return SpelUtils.parse(method, args, rawContent);

        } catch (Exception e) {
            log.error("SpEL 解析失败：{}", e.getMessage());
            return rawContent;
        }
    }

    /**
     * 保存日志
     */
    private void saveLog(String content) {
        Long operatorId = CashierContext.getCurrentId();
        if (operatorId == null) {
            return;
        }

        OperationLog logEntity = new OperationLog();
        logEntity.setOperatorId(operatorId);
        logEntity.setContent(content);

        operationLogMapper.insert(logEntity);
    }
}
