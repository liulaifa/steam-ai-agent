package com.baozi.steamedOrderService.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baozi.steamedOrderService.entity.OrderDetails;
import com.baozi.steamedOrderService.mapper.OrderDetailsMapper;
import com.baozi.steamedOrderService.service.OrderDetailsService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailsServiceImpl extends ServiceImpl<OrderDetailsMapper, OrderDetails> implements OrderDetailsService {
}
