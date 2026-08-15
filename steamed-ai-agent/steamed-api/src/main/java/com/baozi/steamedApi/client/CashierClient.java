package com.baozi.steamedApi.client;

import com.baozi.steamedCommon.domian.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "steamed-cashier-service",
        contextId = "cashierClient"
)
public interface CashierClient {

    /**
     * 根据ID批量查询收银员
     */
    @GetMapping("/batch")
    Result<List<CashierVO>> getCashiersByIds(@RequestParam(value = "ids") List<Long> ids);

   
}
