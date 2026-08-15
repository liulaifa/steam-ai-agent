package com.baozi.steamedApi.client;

import com.baozi.steamedCommon.domian.dto.CartAddDTO;
import com.baozi.steamedCommon.domian.vo.CartVO;
import com.baozi.steamedCommon.domian.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "steamed-cart-service",
        contextId = "cartClient"
)
public interface CartClient {

    /**
     * 查询购物车内的菜品
     */
    @GetMapping
    Result<List<CartVO>> getCart(@RequestParam(value = "cashierId") Long cashierId);

    /**
     * 添加菜品至购物车
     */
    @PostMapping
    Result<Void> addCart(@RequestBody CartAddDTO cartAddDTO);

    /**
     * 清空购物车
     */
    @DeleteMapping
    Result<Void> deleteCart(@RequestParam(value = "cashierId") Long cashierId);


}
