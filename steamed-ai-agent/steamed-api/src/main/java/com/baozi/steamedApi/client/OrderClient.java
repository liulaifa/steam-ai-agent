package com.baozi.steamedApi.client;

import com.baozi.steamedCommon.domian.dto.OrderAddDTO;
import com.baozi.steamedCommon.domian.vo.OrderResultVO;
import com.baozi.steamedCommon.domian.vo.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "steamed-order-service",
        contextId = "orderClient"
)
public interface OrderClient {

    /**
     * 确认下单
     */
    @PostMapping
    Result<OrderResultVO> createOrder(@RequestBody OrderAddDTO dto);
}
