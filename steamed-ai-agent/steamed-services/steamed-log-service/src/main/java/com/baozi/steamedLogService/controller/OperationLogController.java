package com.baozi.steamedLogService.controller;

import com.baozi.steamedCommon.domian.dto.LogPageDTO;
import com.baozi.steamedCommon.domian.vo.LogListVO;
import com.baozi.steamedCommon.domian.vo.PageResult;
import com.baozi.steamedCommon.domian.vo.Result;
import com.baozi.steamedLogService.service.OperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "操作日志")
public class OperationLogController {

    private final OperationLogService operationLogService;

    @Operation(summary = "日志列表查询")
    @PostMapping("/list")
    public Result<PageResult<LogListVO>> getLogList(@RequestBody LogPageDTO dto) {
        PageResult<LogListVO> pageResult = operationLogService.getLogList(dto);
        return Result.success(pageResult);
    }

    @Operation(summary = "清除30天前日志")
    @DeleteMapping("/clean")
    public Result<Integer> cleanLogs() {
        Integer deletedCount = operationLogService.cleanLogs();
        return Result.success(deletedCount);
    }


    @Operation(summary = "导出操作日志")
    @GetMapping("/export")
    public void exportLogs(HttpServletResponse response) {
        operationLogService.exportLogs(response);
    }
}