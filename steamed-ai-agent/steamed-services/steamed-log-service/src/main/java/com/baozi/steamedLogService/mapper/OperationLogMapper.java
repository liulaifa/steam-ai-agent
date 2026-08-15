package com.baozi.steamedLogService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baozi.steamedLogService.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
