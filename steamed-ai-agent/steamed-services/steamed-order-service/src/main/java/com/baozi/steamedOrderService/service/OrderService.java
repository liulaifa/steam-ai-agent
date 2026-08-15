package com.baozi.steamedOrderService.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baozi.steamedCommon.domian.dto.OrderAddDTO;
import com.baozi.steamedCommon.domian.dto.OrderExportDTO;
import com.baozi.steamedCommon.domian.dto.OrderPageDTO;
import com.baozi.steamedCommon.domian.dto.OrderPayDTO;
import com.baozi.steamedOrderService.entity.Orders;
import com.baozi.steamedCommon.domian.vo.OrderVO;
import com.baozi.steamedCommon.domian.vo.OrderListVO;
import com.baozi.steamedCommon.domian.vo.OrderResultVO;
import com.baozi.steamedCommon.domian.vo.PageResult;
import jakarta.servlet.http.HttpServletResponse;


public interface OrderService extends IService<Orders> {

    /**
     * 确认下单
     */
    OrderResultVO createOrder(OrderAddDTO dto);

    /**
     * 订单列表查询
     */
    PageResult<OrderListVO> getOrderList(OrderPageDTO dto);

    /**
     * 订单详情
     */
    OrderVO getOrder(Long id);

    /**
     * 确认收款
     */
    void payOrder(Long id, OrderPayDTO dto);

    /**
     * 开始制作
     */
    void cookOrder(Long id);

    /**
     * 完成制作
     */
    void completeOrder(Long id);

    /**
     * 取消订单
     */
    void cancelOrder(Long id);

    /**
     * 导出订单报表
     */
    void exportOrders(OrderExportDTO dto, HttpServletResponse response);
}
