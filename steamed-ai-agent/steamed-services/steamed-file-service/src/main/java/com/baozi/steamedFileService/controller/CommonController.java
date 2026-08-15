package com.baozi.steamedFileService.controller;


import com.baozi.steamedFileService.properties.OssProperties;
import com.baozi.steamedFileService.util.OssUtil;
import com.baozi.steamedCommon.domian.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/public")
@Tag(name = "文件模块接口")
public class CommonController {

    private final OssProperties ossProperties;

    @Operation(summary = "图片上传")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        try {
            String url = OssUtil.upload(file,ossProperties);
            return Result.success(url);
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败");
        }
    }
}
