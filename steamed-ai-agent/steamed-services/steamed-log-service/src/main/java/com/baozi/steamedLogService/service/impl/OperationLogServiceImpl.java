package com.baozi.steamedLogService.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baozi.steamedApi.client.CashierClient;
import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.domian.dto.LogPageDTO;
import com.baozi.steamedCommon.domian.vo.CashierVO;
import com.baozi.steamedLogService.entity.OperationLog;
import com.baozi.steamedLogService.entity.OperationLogExcel;
import com.baozi.steamedCommon.exception.BusinessException;
import com.baozi.steamedCommon.util.DateUtils;
import com.baozi.steamedCommon.domian.vo.LogListVO;
import com.baozi.steamedCommon.domian.vo.PageResult;
import com.baozi.steamedLogService.mapper.OperationLogMapper;
import com.baozi.steamedLogService.service.OperationLogService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    private final OperationLogMapper operationLogMapper;
    private final CashierClient cashierClient;

    /**
     * 日志列表查询
     */
    public PageResult<LogListVO> getLogList(LogPageDTO dto) {
        // 1. 分页查询日志
        Page<OperationLog> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<OperationLog>()
                .orderByDesc(OperationLog::getCreateTime);

        Page<OperationLog> logPage = operationLogMapper.selectPage(page, wrapper);

        if (logPage.getRecords().isEmpty()) {
            return PageResult.<LogListVO>builder()
                    .total(MessageConstant.ZERO)//0L
                    .page(dto.getPage())
                    .pageSize(dto.getPageSize())
                    .pages(0L)
                    .list(new ArrayList<>())
                    .build();
        }

        // 2. 收集所有操作人ID
        List<Long> operatorIds = logPage.getRecords().stream()
                .map(OperationLog::getOperatorId)
                .distinct()
                .collect(Collectors.toList());

        // 3. 批量查询操作人姓名
        Map<Long, String> operatorNameMap = cashierClient.getCashiersByIds(operatorIds).getData()
                .stream()
                .collect(Collectors.toMap(CashierVO::getId, CashierVO::getRealName));

        // 4. 组装返回结果
        List<LogListVO> logListVOS = BeanUtil.copyToList(logPage.getRecords(), LogListVO.class);
        logListVOS.forEach(log -> log.setOperatorName(operatorNameMap.getOrDefault(log.getOperatorId(), "未知")));
        return PageResult.<LogListVO>builder()
                .total(logPage.getTotal())
                .page(dto.getPage())
                .pageSize(dto.getPageSize())
                .pages(logPage.getPages())
                .list(logListVOS)
                .build();
    }

    /**
     * 清除30天前日志
     */
    public Integer cleanLogs() {
        LocalDateTime deadline = LocalDateTime.now().minusDays(30);

        int deletedCount = operationLogMapper.delete(
                new LambdaQueryWrapper<OperationLog>()
                        .lt(OperationLog::getCreateTime, deadline)
        );

        log.info("【清除30天前日志，共删除{}条记录】", deletedCount);

        return deletedCount;
    }

    /**
     * 导出操作日志
     */
    public void exportLogs(HttpServletResponse response) {
        try {
            // 1. 查询所有日志，按时间升序
            List<OperationLog> logs = operationLogMapper.selectList(
                    new LambdaQueryWrapper<OperationLog>()
                            .orderByAsc(OperationLog::getCreateTime)
            );

            // 2. 收集所有操作人ID，批量查询姓名
            List<Long> operatorIds = logs.stream()
                    .map(OperationLog::getOperatorId)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Long, String> operatorNameMap = new HashMap<>();
            if (!operatorIds.isEmpty()) {
                operatorNameMap = cashierClient.getCashiersByIds(operatorIds).getData()
                        .stream()
                        .collect(Collectors.toMap(CashierVO::getId, CashierVO::getRealName));
            }

            // 3. 转换为 Excel 实体
            List<OperationLogExcel> excelList = new ArrayList<>();
            for (int i = 0; i < logs.size(); i++) {
                OperationLog log = logs.get(i);
                OperationLogExcel excel = new OperationLogExcel();
                excel.setIndex(i + 1);
                excel.setOperatorName(operatorNameMap.getOrDefault(log.getOperatorId(), "未知"));
                excel.setContent(log.getContent());
                excel.setCreateTime(DateUtils.formatDateTime(log.getCreateTime()));
                excelList.add(excel);
            }

            // 4. 设置响应头
            String fileName = "操作日志_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition",
                    "attachment;filename*=utf-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ".xlsx");

            // 5. 写入 Excel
            EasyExcel.write(response.getOutputStream(), OperationLogExcel.class)
                    .sheet("操作日志")
                    .doWrite(excelList);

            log.info("导出操作日志成功，共{}条记录", logs.size());

        } catch (IOException e) {
            log.error("导出操作日志失败", e);
            throw new BusinessException("导出失败：" + e.getMessage());
        }
    }


}
