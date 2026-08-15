package com.baozi.steamedLogService.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baozi.steamedCommon.domian.dto.LogPageDTO;
import com.baozi.steamedLogService.entity.OperationLog;
import com.baozi.steamedCommon.domian.vo.LogListVO;
import com.baozi.steamedCommon.domian.vo.PageResult;
import jakarta.servlet.http.HttpServletResponse;

public interface OperationLogService extends IService<OperationLog> {

    /**
     * 日志列表查询
     */
    PageResult<LogListVO> getLogList(LogPageDTO dto);

    /**
     * 清除30天前日志
     */
    Integer cleanLogs();


    /**
     * 导出操作日志
     */
    void exportLogs(HttpServletResponse response);
}
