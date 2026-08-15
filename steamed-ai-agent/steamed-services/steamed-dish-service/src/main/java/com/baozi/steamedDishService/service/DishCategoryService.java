package com.baozi.steamedDishService.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baozi.steamedCommon.domian.dto.CategoryAddDTO;
import com.baozi.steamedCommon.domian.dto.CategoryUpdateDTO;
import com.baozi.steamedCommon.domian.vo.CategoryListVO;
import com.baozi.steamedCommon.domian.vo.CategoryVO;
import com.baozi.steamedCommon.domian.vo.DishCategoryVO;
import com.baozi.steamedDishService.entity.DishCategory;

import java.util.List;

public interface DishCategoryService extends IService<DishCategory> {

    /**
     * 查询所有菜品分类
     */
    List<DishCategoryVO> getCategory();

    /**
     * 分类列表查询
     */
    List<CategoryListVO> getCategoryList(String name);

    /**
     * 根据ID查询分类信息
     */
    CategoryVO getCategoryById(Long id);

    /**
     * 新增分类
     */
    void addCategory(CategoryAddDTO dto);

    /**
     * 修改分类
     */
    void updateCategory(Long id, CategoryUpdateDTO dto);

    /**
     * 分类状态切换
     */
    Integer toggleCategoryStatus(Long id);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);
}
