package com.baozi.steamedOrderService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baozi.steamedOrderService.entity.OrderDetails;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailsMapper extends BaseMapper<OrderDetails> {
}
