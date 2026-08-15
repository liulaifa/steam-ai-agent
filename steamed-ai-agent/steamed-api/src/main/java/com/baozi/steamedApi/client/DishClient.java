package com.baozi.steamedApi.client;

import com.baozi.steamedCommon.domian.dto.AICartAddDTO;
import com.baozi.steamedCommon.domian.dto.DishFlavorDTO;
import com.baozi.steamedCommon.domian.vo.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "steamed-dish-service",
        contextId = "dishClient"
)
public interface DishClient {
    /**
     * 查询菜品信息根据菜品id
     */
    @GetMapping("/public/detail/{id}")
    Result<DishVO> getDishById(@PathVariable(value = "id") Long id);

    /**
     * 获取热门菜品 Top 10
     */
    @GetMapping("/public/hot")
    Result<List<HotDishVO>> getHotDishes();

    /**
     * 菜品销量 +1
     */
    @PutMapping("/sales/{id}")
    Result<Void> incrementSales(@PathVariable(value = "id") Long id);

    /**
     * 查询菜品口味根据口味id
     */
    @PostMapping("/public/flavor/{flavorId}")
    Result<DishFlavorVO> getDishFlavorByFlavorId(@PathVariable(value = "flavorId") Long flavorId);

    /**
     * 查询菜品口味根据菜品口味DTO
     */
    @PostMapping("/public/flavor/match")
    Result<DishFlavorVO> getDishFlavor(@RequestBody DishFlavorDTO dishFlavorDTO);

    /**
     * 批量查询菜品口味
     */
    @GetMapping("/public/flavor/list")
    Result<List<DishFlavorVO>> getFlavorsByIds(@RequestParam(value = "ids") List<Long> ids);

    /**
     * AI模块的根据菜品名称匹配对应的菜品信息
     */
    @PostMapping("/ai/getDishByName")
    DishVO getDishByName(@RequestBody AICartAddDTO aiCartAddDTO);


}
