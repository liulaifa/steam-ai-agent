package com.baozi.steamedDishService.controller;

import com.baozi.steamedCommon.annotation.Log;
import com.baozi.steamedCommon.domian.dto.CategoryAddDTO;
import com.baozi.steamedCommon.domian.dto.CategoryUpdateDTO;
import com.baozi.steamedCommon.domian.vo.CategoryListVO;
import com.baozi.steamedCommon.domian.vo.CategoryVO;
import com.baozi.steamedCommon.domian.vo.DishCategoryVO;
import com.baozi.steamedCommon.domian.vo.Result;
import com.baozi.steamedDishService.service.DishCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "菜品分类模块接口")
public class DishCategoryController {

    private final DishCategoryService dishCategoryService;

    @Operation(summary = "查询所有菜品分类")
    @GetMapping("/public/categories")
    public Result<List<DishCategoryVO>> getCategory() {
        List<DishCategoryVO> categories = dishCategoryService.getCategory();
        return Result.success(categories);
    }

    @Operation(summary = "分类列表查询")
    @GetMapping("/public/category/list")
    public Result<List<CategoryListVO>> getCategoryList(@RequestParam(required = false) String name) {
        List<CategoryListVO> list = dishCategoryService.getCategoryList(name);
        return Result.success(list);
    }

    @Operation(summary = "根据ID查询分类信息")
    @GetMapping("/public/category/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Long id) {
        CategoryVO category = dishCategoryService.getCategoryById(id);
        return Result.success(category);
    }

    @Operation(summary = "新增分类")
    @PostMapping("/category")
    @Log("新增分类：#dto.name")
    public Result<Void> addCategory(@RequestBody CategoryAddDTO dto) {
        dishCategoryService.addCategory(dto);
        return Result.success(null);
    }

    @Operation(summary = "修改分类")
    @PutMapping("/category/{id}")
    @Log("修改分类：#dto.name")
    public Result<Void> updateCategory(@PathVariable Long id, @RequestBody CategoryUpdateDTO dto) {
        dishCategoryService.updateCategory(id, dto);
        return Result.success(null);
    }

    @Operation(summary = "分类状态切换")
    @PutMapping("/category/status/{id}")
    @Log("分类状态切换：ID=#id")
    public Result<Integer> toggleCategoryStatus(@PathVariable Long id) {
        Integer newStatus = dishCategoryService.toggleCategoryStatus(id);
        return Result.success(newStatus);
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/category/{id}")
    @Log("删除分类：ID=#id")
    public Result<Void> deleteCategory(@PathVariable Long id) {
        dishCategoryService.deleteCategory(id);
        return Result.success(null);
    }

}
