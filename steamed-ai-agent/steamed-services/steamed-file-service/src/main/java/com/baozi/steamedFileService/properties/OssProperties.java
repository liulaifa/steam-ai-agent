package com.baozi.steamedFileService.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "aliyun.oss")
@Component
public class OssProperties {
    private String endpoint;        // 地域节点
    private String accessKeyId;     // 访问密钥ID
    private String accessKeySecret; // 访问密钥密码
    private String bucketName;      // Bucket名称
}
