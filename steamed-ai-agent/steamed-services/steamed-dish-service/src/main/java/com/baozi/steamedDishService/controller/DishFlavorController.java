package com.baozi.steamedDishService.controller;


import com.baozi.steamedCommon.domian.dto.DishFlavorDTO;
import com.baozi.steamedCommon.domian.vo.DishFlavorVO;
import com.baozi.steamedCommon.domian.vo.Result;
import com.baozi.steamedDishService.entity.DishFlavor;
import com.baozi.steamedDishService.service.DishFlavorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public/flavor")
@RequiredArgsConstructor
@Tag(name = "菜品口味模块接口")
public class DishFlavorController {

    private final DishFlavorService dishFlavorService;

    @Operation(summary = "查询菜品口味根据口味id")
    @PostMapping("/{flavorId}")
    public Result<DishFlavorVO> getDishFlavorByFlavorId(@PathVariable Long flavorId){
        return Result.success(dishFlavorService.getDishFlavorByFlavorId(flavorId));
    }

    @Operation(summary = "查询菜品口味根据单个口味DTO")
    @PostMapping("/match")
    public Result<DishFlavorVO> getDishFlavor(@RequestBody DishFlavorDTO dishFlavorDTO){
        DishFlavorVO dishFlavorVO = dishFlavorService.getDishFlavor(dishFlavorDTO);
        return Result.success(dishFlavorVO);
    }

    @Operation(summary = "批量查询菜品口味")
    @GetMapping("/list")
    Result<List<DishFlavor>> getFlavorsByIds(@RequestParam List<Long> ids) {
        return Result.success(dishFlavorService.listByIds(ids));
    }
}
