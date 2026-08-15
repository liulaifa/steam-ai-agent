package com.baozi.steamedCommon.util;

import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.exception.BusinessException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@Getter
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;      // 密钥
    @Value("${jwt.expire:1800000}")
    private Long expire;        // 过期时间默认30分钟（毫秒）



    /**
     * 获取签名密钥
     */
    private  SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成token
     */
    public  String createToken(Long OperationId, Map<String, Object> claims) {
        long now = System.currentTimeMillis();
        // 签发时间
        Date issuedAt = new Date(now);
        // 过期时间
        Date expiration = new Date(now + expire);
        return Jwts.builder()
                .setClaims(claims)        // 自定义载荷
                .setSubject(String.valueOf(OperationId))     // 3. 设置主题（存Id）
                .setIssuedAt(issuedAt)                // 4. 设置签发时间
                .setExpiration(expiration) // 5. 设置过期时间， 是当前时间的毫秒数加上配置的过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 6. 签名
                .compact();                             // 7. 生成字符串
    }

    /**
     * 简化版生成Token（不带额外claims）
     */
    public String createToken(Long userId) {
        return createToken(userId, new HashMap<>());
    }

    /**
     * 获得token
     */
    public  Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从Token中获取用户ID
     */
    public Long getUserId(String token) {
        Claims claims = parseToken(token);
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 验证Token
     */
    public  boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token已过期");
            throw new BusinessException(MessageConstant.TOKEN_EXPIRED);//Token已过期
        } catch (SignatureException e) {
            log.error("Token签名无效");
            throw new BusinessException(MessageConstant.TOKEN_SIGNATURE_INVALID);//Token签名无效
        } catch (MalformedJwtException e) {
            log.error("Token格式错误");
            throw new BusinessException(MessageConstant.TOKEN_FORMAT_ERROR);//Token格式错误
        } catch (IllegalArgumentException e) {
            log.error("Token为空");
            throw new BusinessException(MessageConstant.TOKEN_IS_EMPTY);//Token为空
        } catch (Exception e) {
            log.error("Token非法");
            throw new BusinessException(MessageConstant.TOKEN_INVALID);//Token非法
        }
    }

}
