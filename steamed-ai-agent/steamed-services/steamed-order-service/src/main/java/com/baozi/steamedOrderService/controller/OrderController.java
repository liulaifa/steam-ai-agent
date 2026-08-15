package com.baozi.steamedOrderService.controller;

import com.baozi.steamedCommon.domian.vo.*;
import com.baozi.steamedCommon.domian.dto.OrderAddDTO;
import com.baozi.steamedCommon.domian.dto.OrderExportDTO;
import com.baozi.steamedCommon.domian.dto.OrderPageDTO;
import com.baozi.steamedCommon.domian.dto.OrderPayDTO;
import com.baozi.steamedOrderService.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "订单模块接口")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "确认下单")
    @PostMapping
    public Result<OrderResultVO> createOrder(@RequestBody OrderAddDTO dto) {
        OrderResultVO result = orderService.createOrder(dto);
        return Result.success(result);
    }

    @Operation(summary = "订单列表查询")
    @PostMapping("/list")
    public Result<PageResult<OrderListVO>> getOrderList(@RequestBody OrderPageDTO dto) {
        PageResult<OrderListVO> pageResult = orderService.getOrderList(dto);
        return Result.success(pageResult);
    }

    @Operation(summary = "订单详情")
    @GetMapping("/{id}")
    public Result<OrderVO> getOrder(@PathVariable Long id) {
        OrderVO detail = orderService.getOrder(id);
        return Result.success(detail);
    }

    @Operation(summary = "确认收款")
    @PutMapping("/pay/{id}")
    public Result<Void> payOrder(@PathVariable Long id, @RequestBody OrderPayDTO dto) {
        orderService.payOrder(id, dto);
        return Result.success(null);
    }

    @Operation(summary = "开始制作")
    @PutMapping("/cook/{id}")
    public Result<Void> cookOrder(@PathVariable Long id) {
        orderService.cookOrder(id);
        return Result.success(null);
    }

    @Operation(summary = "完成制作")
    @PutMapping("/complete/{id}")
    public Result<Void> completeOrder(@PathVariable Long id) {
        orderService.completeOrder(id);
        return Result.success(null);
    }

    @Operation(summary = "取消订单")
    @PutMapping("/cancel/{id}")
    public Result<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return Result.success(null);
    }

    @Operation(summary = "导出订单报表")
    @PostMapping("/export")
    public void exportOrders(@RequestBody OrderExportDTO dto, HttpServletResponse response) {
        orderService.exportOrders(dto, response);
    }
}
