package com.baozi.steamedFileService.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectResult;
import com.baozi.steamedFileService.properties.OssProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class OssUtil {

    private OssUtil(){}

    /**
     * 上传文件到 OSS
     * @param file 文件
     * @return 文件访问 URL
     */
    public static String upload(MultipartFile file,OssProperties ossProperties) throws IOException {
        // 1. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = "dish/" + UUID.randomUUID().toString().replace("-", "") + extension;

        // 2. 创建 OSS 客户端
        OSS ossClient = new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret()
        );

        try {
            // 3. 上传文件
            PutObjectResult result = ossClient.putObject(
                    ossProperties.getBucketName(),
                    fileName,
                    file.getInputStream()
            );

            log.info("文件上传成功：{}", fileName);

            // 4. 返回访问 URL
            return "https://" + ossProperties.getBucketName() + "." + ossProperties.getEndpoint() + "/" + fileName;

        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败：" + e.getMessage());
        } finally {
            // 5. 关闭客户端
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}
