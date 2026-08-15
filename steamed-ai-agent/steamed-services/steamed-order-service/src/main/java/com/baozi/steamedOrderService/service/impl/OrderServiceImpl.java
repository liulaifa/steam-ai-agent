package com.baozi.steamedOrderService.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baozi.steamedApi.client.CartClient;
import com.baozi.steamedApi.client.DishClient;
import com.baozi.steamedCommon.domian.vo.*;
import com.baozi.steamedCommon.util.DateUtils;
import com.baozi.steamedCommon.util.IsLoginUtil;
import com.baozi.steamedOrderService.entity.OrderDetailExcel;
import com.baozi.steamedOrderService.entity.OrderDetails;
import com.baozi.steamedOrderService.entity.OrderSummaryExcel;
import com.baozi.steamedOrderService.entity.Orders;
import com.baozi.steamedOrderService.util.FlavorTextUtils;
import com.baozi.steamedOrderService.util.OrderNumberGeneratorUtil;
import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.constant.OrderStatusConstant;
import com.baozi.steamedCommon.context.CashierContext;
import com.baozi.steamedCommon.domian.dto.OrderAddDTO;
import com.baozi.steamedCommon.domian.dto.OrderExportDTO;
import com.baozi.steamedCommon.domian.dto.OrderPageDTO;
import com.baozi.steamedCommon.domian.dto.OrderPayDTO;
import com.baozi.steamedCommon.exception.BusinessException;
import com.baozi.steamedOrderService.mapper.OrderMapper;
import com.baozi.steamedOrderService.service.OrderDetailsService;
import com.baozi.steamedOrderService.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    private final CartClient cartClient;
    private final DishClient dishClient;
    private final OrderDetailsService orderDetailsService;
    private final OrderMapper orderMapper;

    /**
     * 确认下单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderResultVO createOrder(OrderAddDTO dto) {
        // 1. 获取当前收银员ID
        Long cashierId = CashierContext.getCurrentId();
        IsLoginUtil.isLogin(cashierId);

        // 2. 查询购物车
        List<CartVO> cartList = BeanUtil.copyToList(cartClient.getCart(cashierId).getData(), CartVO.class);

        if (cartList.isEmpty()) {
            throw new BusinessException(MessageConstant.CART_IS_EMPTY);//购物车为空，无法下单
        }

        // 3. 计算总金额
        int totalPrice = cartList.stream()
                .mapToInt(cart -> cart.getPrice() * cart.getNumber())
                .sum();

        // 4. 插入订单主表
        Orders order = BeanUtil.copyProperties(dto, Orders.class);
        order.setOrderNumber(OrderNumberGeneratorUtil.generate());
        order.setPrice(totalPrice);
        order.setStatus(OrderStatusConstant.PENDING_PAYMENT);//1：待支付
        order.setCashierId(cashierId);

        orderMapper.insert(order);

        // 5. 批量插入订单明细
        List<OrderDetails> orderDetails = BeanUtil.copyToList(cartList, OrderDetails.class);
        orderDetails.forEach(orderDetail->{
            orderDetail.setOrderId(order.getId());
            orderDetail.setStatus(OrderStatusConstant.GET_DISH);
        });
        orderDetailsService.saveBatch(orderDetails);

        // 6. 清空购物车
        cartClient.deleteCart(cashierId);

        // 7.更新redis销量
        orderDetails.forEach(orderDetail -> {
            dishClient.incrementSales(orderDetail.getDishId());
        });
        log.info("【下单成功：订单ID={}, 订单流水号={}】", order.getId(), order.getOrderNumber());

        return OrderResultVO.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .build();
    }

    /**
     * 订单列表查询
     */
    @Transactional(rollbackFor = Exception.class)
    public PageResult<OrderListVO> getOrderList(OrderPageDTO dto) {
        //查询多个订单列表(前端显示结果)，每个订单都有自己的订单菜品列，每个订单菜品类都有自己的菜品和菜品口味信息
        //前端传来的参数是查询的多个订单列表的限制条件
        // 构建订单列表查询条件（加筛选条件）
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();

        if (dto.getStartDate() != null) {
            wrapper.ge(Orders::getCreateTime, dto.getStartDate());
        }
        if (dto.getEndDate() != null) {
            wrapper.le(Orders::getCreateTime, dto.getEndDate());
        }
        if (dto.getStatus() != null) {
            wrapper.eq(Orders::getStatus, dto.getStatus());
        }

        // 分页查询
        Page<Orders> page = new Page<>(dto.getPage(), dto.getPageSize());
        // 按创建时间倒序，最新订单在最前面
        wrapper.orderByDesc(Orders::getCreateTime);
        Page<Orders> orderPage = page(page, wrapper);

        // 订单列表为空时返回空的页面
        if (orderPage.getRecords().isEmpty()) {
            return PageResult.<OrderListVO>builder()
                    .total(0L)
                    .page(dto.getPage())
                    .pageSize(dto.getPageSize())
                    .pages(0L)
                    .list(new ArrayList<>())
                    .build();
        }
        // 查询多个订单列表信息（加筛选条件），返回的是符合参数限制的订单列表，但是还缺少VO中的List<OrderItemVO>
        List<Orders> orders = orderPage.getRecords();
        List<OrderListVO> orderListVOS = BeanUtil.copyToList(orders, OrderListVO.class);
        //查询List<OrderItemVO>需要的数据，也就是每个订单都有自己的订单菜品列，但是每个订单菜品列都有自己的菜品和菜品口味信息


        //获取订单ID
        List<Long> OrderIds = orders.stream().map(Orders::getId).distinct().toList();
        //根据订单ID获取订单菜品列
        List<OrderDetails> orderDetails = orderDetailsService.list(
                new LambdaQueryWrapper<OrderDetails>()
                        .in(OrderDetails::getOrderId, OrderIds)
        );
        //将订单菜品列转换为VO，但是OrderItemVO中还有DishFlavorVO没有补充
        List<OrderDetailItemVO> orderItemVOS = BeanUtil.copyToList(orderDetails, OrderDetailItemVO.class);

        //查询所有的口味id，通过口味id获取到口味信息，然后封装到Mpa里面（口味id，口味信息VO）

        // 收集所有口味ID
        List<Long> flavorIds = orderItemVOS.stream().map(OrderDetailItemVO::getFlavorId).filter(Objects::nonNull).distinct().toList();
        // 批量查询口味
        Map<Long, DishFlavorVO> flavorMap = flavorIds.isEmpty()
                ? Collections.emptyMap()
                : Optional.ofNullable(dishClient.getFlavorsByIds(flavorIds))
                .map(Result::getData)
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(DishFlavorVO::getId, f -> f));
        //补充每一个OrderItemVO中的DishFlavorVO
        orderItemVOS.forEach(orderItemVO -> {
            DishFlavorVO flavor = flavorMap.get(orderItemVO.getFlavorId());
            if (flavor == null) {
                orderItemVO.setDishFlavorVO(DishFlavorVO.builder().build());  // 设置空对象，避免前端拿到 null
            } else {
                orderItemVO.setDishFlavorVO(flavor);
            }
        });
        //将完成的OrderItemVO转换为Map。（订单ID，OrderItemVO）
        Map<Long, List<OrderDetailItemVO>> ItemsMap = orderItemVOS.stream()
                .filter(item -> item.getOrderId() != null)
                .collect(Collectors.groupingBy(OrderDetailItemVO::getOrderId));
       // 将ItemsMap通过订单id查询到对应的value值返回list集合，最后封装进OrderListVO中
        orderListVOS.forEach(
                orderListVO -> {
                    List<OrderDetailItemVO> items = ItemsMap.getOrDefault(orderListVO.getId(), Collections.emptyList());
                    orderListVO.setItems(items);
                }
        );
        return PageResult.<OrderListVO>builder()
                .total(orderPage.getTotal())
                .page(dto.getPage())
                .pageSize(dto.getPageSize())
                .pages(orderPage.getPages())
                .list(orderListVOS)
                .build();
    }

    /**
     * 查询订单
     */
    @Transactional(rollbackFor = Exception.class)
    public OrderVO getOrder(Long id) {
        // 1. 查询订单
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(MessageConstant.ORDER_IS_EMPTY);
        }
        //将订单明细整合到订单中
        // 2. 查询订单明细
        List<OrderDetails> orderDetail = orderDetailsService.list(
                new LambdaQueryWrapper<OrderDetails>()
                        .eq(OrderDetails::getOrderId, id)
        );
        // 将订单详情的信息转化为OrderDetailItemVO
        List<OrderDetailItemVO> orderDetailItemVOS = BeanUtil.copyToList(orderDetail, OrderDetailItemVO.class);
        // 3. 获取订单详情中菜品口味的ids
        List<Long> flavorIds = orderDetail.stream().map(OrderDetails::getFlavorId).filter(Objects::nonNull).distinct().toList();
        // 将菜品口味转化为Map集合方便取
        Map<Long, DishFlavorVO> flavorMap = flavorIds.isEmpty() ?
                Collections.emptyMap() :
                        BeanUtil.copyToList(dishClient.getFlavorsByIds(flavorIds).getData(), DishFlavorVO.class)
                        .stream()
                        .collect(Collectors.toMap(DishFlavorVO::getId, f -> f, (v1, v2) -> v1));
        // 填充菜品口味信息
        orderDetailItemVOS.forEach(orderDetailItemVO -> {
            orderDetailItemVO.setDishFlavorVO(flavorMap.get(orderDetailItemVO.getFlavorId()));
        });
        // 4. 组装返回数据
        OrderVO orderVO = BeanUtil.copyProperties(order, OrderVO.class);
        orderVO.setItems(orderDetailItemVOS);
        return orderVO;
    }

    /**
     * 确认收款
     */
    public void payOrder(Long id, OrderPayDTO dto) {
        // 1. 查询订单
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(MessageConstant.ORDER_IS_EMPTY);//订单不存在
        }

        // 2. 校验状态
        if (!OrderStatusConstant.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new BusinessException(MessageConstant.ORDER_IS_ERROR);//订单状态不正确
        }

        // 3. 更新订单
        order.setStatus(OrderStatusConstant.PAID);
        order.setPayMethod(dto.getPayMethod());
        order.setPayTime(LocalDateTime.now());

        orderMapper.updateById(order);

        log.info("【确认收款成功：orderId={}, payMethod={}】", id, dto.getPayMethod());
    }

    /**
     * 开始制作
     */
    public void cookOrder(Long id) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(MessageConstant.ORDER_IS_EMPTY);//订单不存在
        }

        if (!OrderStatusConstant.PAID.equals(order.getStatus())) {
            throw new BusinessException(MessageConstant.ORDER_IS_ERROR);//订单状态不正确
        }

        order.setStatus(OrderStatusConstant.COOKING);
        orderMapper.updateById(order);

        log.info("【开始制作：orderId={}】", id);
    }

    /**
     * 完成制作
     */
    public void completeOrder(Long id) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(MessageConstant.ORDER_IS_EMPTY);//订单不存在
        }

        if (!OrderStatusConstant.COOKING.equals(order.getStatus())) {
            throw new BusinessException(MessageConstant.ORDER_IS_ERROR);//订单状态不正确
        }

        order.setStatus(OrderStatusConstant.COMPLETED);
        orderMapper.updateById(order);

        log.info("【完成制作：orderId={}】", id);
    }

    /**
     * 取消订单
     */
    public void cancelOrder(Long id) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(MessageConstant.ORDER_IS_EMPTY);//订单不存在
        }

        if (!OrderStatusConstant.PENDING_PAYMENT.equals(order.getStatus())) {
            throw new BusinessException(MessageConstant.ORDER_IS_ERROR);//订单状态不正确
        }

        order.setStatus(OrderStatusConstant.CANCELLED);
        orderMapper.updateById(order);

        log.info("【取消订单：orderId={}】", id);
    }

    /**
     * 导出订单报表
     */
    public void exportOrders(OrderExportDTO dto, HttpServletResponse response) {
        try {
            // 1. 构建查询条件
            LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();

            LocalDateTime startDateTime = DateUtils.parseStartOfDay(dto.getStartDate());
            LocalDateTime endDateTime = DateUtils.parseEndOfDay(dto.getEndDate());

            if (startDateTime != null) {
                wrapper.ge(Orders::getCreateTime, startDateTime);
            }
            if (endDateTime != null) {
                wrapper.le(Orders::getCreateTime, endDateTime);
            }
            if (dto.getStatus() != null) {
                wrapper.eq(Orders::getStatus, dto.getStatus());
            }
            if (dto.getDineType() != null) {
                wrapper.eq(Orders::getDineType, dto.getDineType());
            }

            wrapper.orderByAsc(Orders::getCreateTime);

            // 2. 查询订单主表
            List<Orders> orders = orderMapper.selectList(wrapper);

            if (orders.isEmpty()) {
                throw new BusinessException("没有符合条件的订单数据");
            }

            // 3. 收集订单ID，批量查询订单明细
            List<Long> orderIds = orders.stream()
                    .map(Orders::getId)
                    .collect(Collectors.toList());

            List<OrderDetails> allDetails = orderDetailsService.list(
                    new LambdaQueryWrapper<OrderDetails>()
                            .in(OrderDetails::getOrderId, orderIds)
            );

            Map<Long, List<OrderDetails>> detailsMap = allDetails.stream()
                    .collect(Collectors.groupingBy(OrderDetails::getOrderId));

            // 4. 批量查询口味
            List<Long> flavorIds = allDetails.stream()
                    .map(OrderDetails::getFlavorId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Long, DishFlavorVO> flavorMap = flavorIds.isEmpty() ?
                    Collections.emptyMap() :
                    BeanUtil.copyToList(
                                    dishClient.getFlavorsByIds(flavorIds).getData(),
                                    DishFlavorVO.class
                            )
                            .stream()
                            .collect(Collectors.toMap(DishFlavorVO::getId, f -> f, (v1, v2) -> v1));

            // 5. 构建汇总数据和明细数据
            List<OrderSummaryExcel> summaryList = new ArrayList<>();
            List<OrderDetailExcel> detailList = new ArrayList<>();

            for (Orders order : orders) {
                // 汇总数据
                OrderSummaryExcel summary = new OrderSummaryExcel();
                summary.setOrderNumber(order.getOrderNumber());
                summary.setCreateTime(DateUtils.formatDateTime(order.getCreateTime()));
                summary.setDineType(order.getDineType() == 1 ? "堂食" : "打包");
                summary.setStatus(getStatusText(order.getStatus()));
                summary.setTotalPrice(order.getPrice());
                summary.setRemark(order.getRemark() != null ? order.getRemark() : "");
                summaryList.add(summary);

                // 明细数据
                List<OrderDetails> details = detailsMap.getOrDefault(order.getId(), new ArrayList<>());
                for (OrderDetails detail : details) {
                    OrderDetailExcel detailExcel = new OrderDetailExcel();
                    detailExcel.setOrderNumber(order.getOrderNumber());
                    detailExcel.setDishName(detail.getDishName());
                    detailExcel.setFlavorText(FlavorTextUtils.build(flavorMap.get(detail.getFlavorId())));
                    detailExcel.setPrice(detail.getPrice());
                    detailExcel.setNumber(detail.getNumber());
                    detailExcel.setSubtotal(detail.getPrice() * detail.getNumber());
                    detailList.add(detailExcel);
                }
            }

            // 6. 设置响应头
            String fileName = "订单报表_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition",
                    "attachment;filename*=utf-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ".xlsx");

            // 7. 写入 Excel（两个 Sheet）
            OutputStream outputStream = response.getOutputStream();
            ExcelWriter writer = EasyExcel.write(outputStream).build();

            WriteSheet sheet1 = EasyExcel.writerSheet(0, "订单汇总").head(OrderSummaryExcel.class).build();
            writer.write(summaryList, sheet1);

            WriteSheet sheet2 = EasyExcel.writerSheet(1, "订单明细").head(OrderDetailExcel.class).build();
            writer.write(detailList, sheet2);

            writer.finish();

            log.info("导出订单成功，共{}个订单，{}条明细", orders.size(), detailList.size());

        } catch (Exception e) {
            log.error("导出订单失败", e);
            throw new BusinessException("导出失败：" + e.getMessage());
        }
    }
    /**
     * 获取订单状态文本
     */
    private String getStatusText(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 1 -> "待支付";
            case 2 -> "已支付";
            case 3 -> "制作中";
            case 4 -> "已完成";
            case 5 -> "已取消";
            default -> "未知";
        };
    }


}
