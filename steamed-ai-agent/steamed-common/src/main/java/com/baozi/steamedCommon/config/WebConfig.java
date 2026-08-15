package com.baozi.steamedCommon.config;

import com.baozi.steamedCommon.interceptor.JwtInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnClass(HttpServletRequest.class)//
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(
                        "/public/**",
                        "/doc.html",
                        "/v3/api-docs/**",    // 匹配 /v3/api-docs/xxx
                        "/swagger-ui/**"     // 匹配 /swagger-ui/xxx
                );
    }
}