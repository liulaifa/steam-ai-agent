package com.baozi.steamedGateway.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//@ConfigurationProperties(prefix = "jwt")
//@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtProperties {
    private String secret;      // 密钥
    private Long expire;        // 过期时间（毫秒）
    private String tokenPrefix; // Token 前缀
    private String header;      // 请求头名称
}
