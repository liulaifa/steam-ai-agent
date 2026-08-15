package com.baozi.steamedCommon.interceptor;

import cn.hutool.core.util.StrUtil;
import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.context.CashierContext;
import com.baozi.steamedCommon.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

@Component
@ConditionalOnClass(HttpServletRequest.class)//
@Slf4j
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // 获取请求头中的 X-User-Id
        String id = request.getHeader("X-User-Id");
        String secret = request.getHeader("X-Gateway-Secret");


        // ✅ 必须做非空校验
        if (StrUtil.isBlank(id) || !("20260726".equals(secret))) {
            log.warn("请求缺少 X-User-Id 或者 X-Gateway-Secret 请求头，可能绕过网关直接访问");
            throw new BusinessException(MessageConstant.ID_IS_NULL);
        }
        // ✅ 必须做格式校验（防止恶意注入）
        try {
            Long parsedId = Long.parseLong(id);
            //将请求头X-User-Id的值，存入 CashierContext中的ThreadLocal
            CashierContext.setCurrentId(parsedId);
            log.info("【X-User-Id有效：{}】", parsedId);
        } catch (NumberFormatException e) {
            log.warn("X-User-Id 格式非法: {}", id);
            throw new BusinessException(MessageConstant.ID_FORMAT_ERROR);
        }
        return true;
    }

    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response) {
        // 请求结束后清理 ThreadLocal，防止内存泄漏
        CashierContext.remove();
    }
}
