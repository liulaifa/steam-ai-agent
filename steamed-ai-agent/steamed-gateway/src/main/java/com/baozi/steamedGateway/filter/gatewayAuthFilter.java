package com.baozi.steamedGateway.filter;

import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.context.CashierContext;
import com.baozi.steamedCommon.util.JwtUtil;
import com.baozi.steamedGateway.vo.Result;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.netty.handler.codec.http.HttpConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Order(-1)  // 优先级最高
@RequiredArgsConstructor
@Slf4j
//网关的全局过滤器，用于登录的token校验
public class gatewayAuthFilter implements GlobalFilter {

    private final AntPathMatcher matcher = new AntPathMatcher();
    private final JwtUtil jwtUtil;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1.  获取请求
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        // 1.1. 获取请求路径
        String path = request.getURI().getPath();
        // 1.2 放行接口文档
        if (path.contains("/v3/api-docs")
                || path.contains("/swagger-ui")
                || path.contains("/doc.html")) {
            return chain.filter(exchange);
        }
        //1.2. 判断是否放行
        if (isExcluded(path)) {
            //放行不需要校验的请求，比如用户的登录、登出、获取公开资源
            return chain.filter(exchange);
        }
        //1.3. 获取请求头的token
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        // 2. 校验 Token
        // 2.1 检查 Token 是否为空
        if (token == null || token.isEmpty()) {
            // Token 为空，返回“Token为空”错误响应
            return writeErrorResponse(response, MessageConstant.TOKEN_IS_EMPTY);
        }
        // 3. 解析 Token，提取用户信息
        try {
            // 3.1 从 Token 中解析用户信息
            Claims claims = jwtUtil.parseToken(token);
            String userId = claims.getSubject();

            // 3.3 刷新 Token（无感知续期）
            // 生成Token
            String newToken = jwtUtil.createToken(Long.parseLong(userId), claims);
            ServerHttpRequest newRequest = request.mutate()
                    .header(HttpHeaders.AUTHORIZATION, newToken)//token
                    .header("X-User-Id", userId)//X-User-Id
                    .header("X-Gateway-Secret", "20260726")//X-Gateway-Secret
                    .build();
            //  将请求头X-User-Id的值，存入 CashierContext中的ThreadLocal
            CashierContext.remove();
            CashierContext.setCurrentId(Long.parseLong(userId));
            // 放行
            log.info("转发请求头Authorization: {}", newRequest.getHeaders().getFirst(HttpHeaders.AUTHORIZATION));
            // 执行后续过滤器链（使用新的请求对象），并在所有过滤器执行完毕后
            // 通过 then(Mono.fromRunnable(...)) 再响应返回前将新 Token 放入响应头
            return chain.filter(exchange.mutate().request(newRequest).build())
                    .doFinally(signalType -> {
                        if (!exchange.getResponse().isCommitted()) {
                            exchange.getResponse().getHeaders().set(HttpHeaders.AUTHORIZATION, newToken);
                        }
                    });
        } catch (ExpiredJwtException e) {
            log.warn("Token已过期: {}", token);
            return writeErrorResponse(response,MessageConstant.TOKEN_EXPIRED);
        } catch (MalformedJwtException | SignatureException e) {
            log.warn("Token签名无效: {}", token);
            return writeErrorResponse(response,MessageConstant.TOKEN_SIGNATURE_INVALID);
        } catch (Exception e) {
            log.error("JWT解析异常", e);
            return writeErrorResponse(response,MessageConstant.TOKEN_INVALID);
        }
    }

    private Mono<Void> writeErrorResponse(ServerHttpResponse response, String Message) {
        // 设置响应内容类型为 JSON
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String jsonStr = JSONUtil.toJsonStr(Result.error(Message));
        if (jsonStr == null) {
            return response.writeWith(Mono.error(new RuntimeException("响应序列化失败")));
        }
        byte[] bytes = jsonStr.getBytes(StandardCharsets.UTF_8);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
    }

    private boolean isExcluded(String path) {
        return EXCLUDE_PATHS.stream()
                .anyMatch(pattern -> matcher.match(pattern, path));
    }
    // 放行路径列表，不做token校验
    private static final List<String> EXCLUDE_PATHS = List.of(
            "/cashier/public/**",
            "/dish/public/**",
            // 文件上传
            "/file/**"

    );


}