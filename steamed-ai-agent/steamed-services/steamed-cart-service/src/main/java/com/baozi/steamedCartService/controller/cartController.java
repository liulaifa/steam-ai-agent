package com.baozi.steamedCartService.controller;


import com.baozi.steamedCartService.service.CartService;
import com.baozi.steamedCommon.domian.dto.CartAddDTO;
import com.baozi.steamedCommon.domian.dto.CartUpdateDTO;
import com.baozi.steamedCommon.domian.vo.CartVO;
import com.baozi.steamedCommon.domian.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "购物车模块")
public class cartController {

    private final CartService cartService;

    @Operation(summary = "查询购物车内菜品")
    @GetMapping
    public List<CartVO> getCart(@RequestParam(value = "cashierId") Long cashierId) {
        return cartService.getCart(cashierId);
    }

    @Operation(summary = "添加菜品至购物车")
    @PostMapping
    public Result<Void> addCart(@RequestBody CartAddDTO cartAddDTO){
        cartService.add(cartAddDTO);
        return Result.success();
    }


    @Operation(summary = "修改购物车内的菜品数量(+加|-减)")
    @PutMapping
    public Result<Void> updateCart(@RequestBody CartUpdateDTO cartUpdateDTO){
        cartService.update(cartUpdateDTO);
        return Result.success();
    }

    @Operation(summary = "删除购物车内目标菜品所有数量")
    @DeleteMapping("/{CartId}")
    public Result<Void> deleteCart(@PathVariable Long CartId){
        cartService.delete(CartId);
        return Result.success();
    }

    @Operation(summary = "清空购物车")
    @DeleteMapping
    public Result<Void> deleteCart(){
        cartService.clear();
        return Result.success();
    }
}
