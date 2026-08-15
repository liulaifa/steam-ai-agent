package com.baozi.steamedApi.Interceptor;

import cn.hutool.core.util.StrUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
//@ConfigurationProperties(prefix = "header") // 从application.yml 中获取配置前缀的值
@Slf4j
//拦截当前线路的请求头数据构建新的请求头信息然后转发给下一个微服务
public class FeignAuthInterceptor implements RequestInterceptor {

    //简易版的请求头转发
    /*private final JwtUtil jwtUtil;
    @Override
    public void apply(RequestTemplate template) {
        Long id = CashierContext.getCurrentId();
        String token = jwtUtil.createToken(id, new HashMap<>());
        if (!StrUtil.isBlank(token)) {
            template.header(jwtUtil.getHeader(), token);
        }
    }*/

    /**
     * 需要透传的 Header 白名单
     * 只有在此列表中的 Header 才会被透传
     * 如果列表为空，则透传所有不在排除列表中的 Header
     */
    private Set<String> alloweds = new HashSet<>();

    /**
     * ========== Bean 初始化方法 ==========
     * <p>
     * 功能说明：
     * 1. 使用 @PostConstruct 注解，在 Bean 的所有依赖注入完成后自动执行
     * 2. 检查白名单是否为空（可能因配置文件缺失或加载失败导致）
     * 3. 如果白名单为空，则添加默认的核心 Header，确保系统基本功能可用
     * 4. 提供降级方案，防止配置缺失导致认证信息无法透传
     * </p>
     * <p>
     * 执行时机：
     * Spring 容器在完成 HeaderInterceptor Bean 的实例化和依赖注入后，
     * 自动调用该方法进行初始化，保证后续使用前白名单已准备好。
     * </p>
     * <p>
     * 为什么需要默认值？
     * 1. 配置文件可能缺失或未正确加载，导致 alloweds 为空
     * 2. 即使配置缺失，也要保证基础认证 Header（Authorization）能够透传
     * 3. 提高系统的容错性和健壮性
     * </p>
     *
     */
    @PostConstruct
    public void init() {
        // 检查白名单是否为空（配置未加载或配置文件中未配置）
        if (alloweds.isEmpty()) {
            // 添加默认的核心 Header，确保认证和用户信息能够正常传递
            // 这些是微服务间调用最常用的认证和追踪 Header
            alloweds.add("Authorization");  // Token 认证
            alloweds.add("X-User-Id");      // 用户 ID
            alloweds.add("X-Gateway-Secret"); // 网关密钥
//            alloweds.add("X-User-Role");    // 用户角色
//            alloweds.add("X-Trace-Id");     // 链路追踪 D
            // 记录日志，提示使用了默认配置（便于运维排查）
            log.info("配置文件中未配置白名单，使用默认 Header 白名单: {}", alloweds);
        } else {
            // 记录加载的配置（调试用）
            log.debug("白名单已加载: {}", alloweds);
        }
    }

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 1. 获取当前线程的 Web 请求上下文
        ServletRequestAttributes attributes = (ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            // 没有 Web 请求上下文（如定时任务、MQ 消费、内部调用等场景）
            log.info("当前线程无 Web 请求上下文，跳过 Header 透传");
            return;
        }
        HttpServletRequest request = attributes.getRequest();

        //  2. 获取所有请求头名称
        // 获取当前 HTTP 请求中的所有请求头（Header）的名称列表，返回一个 Enumeration（枚举）对象，用于遍历所有的请求头名称
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) {
            log.info("请求头为空，跳过 Header 透传");
            return;
        }

        //  3. 遍历并透传请求头
        // hasMoreElements:判断枚举中是否还有更多元素,类似于set迭代器
        // 透传所有白名单请求头,默认请求头中只有一个value
        alloweds.forEach(headerName -> {
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues != null && headerValues.hasMoreElements()) {
                String headerValue = headerValues.nextElement();
                if (StrUtil.isNotBlank(headerValue)) {
                    requestTemplate.header(headerName, headerValue);
                }
            }
        });
    }
}