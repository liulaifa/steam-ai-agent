package com.baozi.steamedDishService.controller;


import com.baozi.steamedCommon.annotation.Log;
import com.baozi.steamedCommon.domian.dto.AICartAddDTO;
import com.baozi.steamedCommon.domian.vo.*;
import com.baozi.steamedCommon.domian.dto.DishAddDTO;
import com.baozi.steamedCommon.domian.dto.DishPageDTO;
import com.baozi.steamedCommon.domian.dto.DishUpdateDTO;
import com.baozi.steamedDishService.entity.Dish;
import com.baozi.steamedDishService.service.DishService;
import com.baozi.steamedDishService.util.HotDishRedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor

@Tag(name = "菜品模块接口")
public class DishController {

    private final DishService dishService;
    private final HotDishRedisUtil hotDishRedisUtil;

    @Operation(summary = "查询菜品根据菜品分类id")
    @GetMapping("/public/{categoryId}")
    public Result<List<DishVO>> getDishByCategoryId(@PathVariable Integer categoryId){
        List<DishVO> dishVO = dishService.getDishesByCategoryId(categoryId);
        return Result.success(dishVO);
    }

    @Operation(summary = "查询菜品信息根据菜品id")
    @GetMapping("/public/detail/{id}")
    public Result<DishVO> getDishById(@PathVariable Integer id){
        DishVO dishVo = dishService.getDishById(id);
        return Result.success(dishVo);
    }


    @Operation(summary = "查询所有菜品")
    @GetMapping("/public/all")
    public Result<List<DishVO>> getDishes(){
        List<DishVO> dishVO = dishService.getAll();
        return Result.success(dishVO);
    }

    @Operation(summary = "菜品分页查询")
    @PostMapping("/public/page")
    public Result<PageResult<DishPageVO>> pageQuery(@RequestBody DishPageDTO dto) {
        PageResult<DishPageVO> pageResult = dishService.pageQuery(dto);
        return Result.success(pageResult);
    }

    @Operation(summary = "新增菜品")
    @PostMapping()
    @Log("新增菜品：#dto.name")
    public Result<Void> addDish(@RequestBody DishAddDTO dto) {
        dishService.addDish(dto);
        return Result.success(null);
    }

    @Operation(summary = "修改菜品")
    @PutMapping("/{id}")
    @Log("修改菜品：#dto.name")
    public Result<Void> updateDish(@PathVariable Long id, @RequestBody DishUpdateDTO dto) {
        dishService.updateDish(id, dto);
        return Result.success(null);
    }

    @Operation(summary = "菜品状态切换")
    @PutMapping("/status/{id}")
    @Log("菜品状态切换：ID=#id")
    public Result<Integer> toggleStatus(@PathVariable Long id) {
        Integer newStatus = dishService.toggleStatus(id);
        return Result.success(newStatus);
    }

    @Operation(summary = "删除菜品")
    @DeleteMapping("/{id}")
    @Log("删除菜品：ID=#id")
    public Result<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return Result.success(null);
    }

    @Operation(summary = "获取热门菜品 Top 10")
    @GetMapping("/public/hot")
    public Result<List<HotDishVO>> getHotDishes() {
        List<HotDishVO> hotDishes = dishService.getHotDishes();
        return Result.success(hotDishes);
    }

    @Operation(summary = "菜品销量 +1")
    @PutMapping("/sales/{id}")
    Result<Void> incrementSales(@PathVariable Long id) {
        hotDishRedisUtil.incrementSales(id);
        return Result.success(null);
    }

    @Operation(summary = "AI模块的根据菜品名称匹配对应的菜品信息")
    @PostMapping("/ai/getDishByName")
    public DishVO getDishByName(@RequestBody AICartAddDTO aiCartAddDTO){
        return dishService.getDishByName(aiCartAddDTO);
    }
}
