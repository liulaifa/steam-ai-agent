package com.baozi.steamedDishService.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.constant.StatusConstant;
import com.baozi.steamedCommon.domian.dto.CategoryAddDTO;
import com.baozi.steamedCommon.domian.dto.CategoryUpdateDTO;
import com.baozi.steamedCommon.exception.BusinessException;
import com.baozi.steamedCommon.domian.vo.CategoryListVO;
import com.baozi.steamedCommon.domian.vo.CategoryVO;
import com.baozi.steamedCommon.domian.vo.DishCategoryVO;
import com.baozi.steamedCommon.util.IsLoginUtil;
import com.baozi.steamedDishService.entity.Dish;
import com.baozi.steamedDishService.entity.DishCategory;
import com.baozi.steamedDishService.mapper.DishCategoryMapper;
import com.baozi.steamedDishService.mapper.DishMapper;
import com.baozi.steamedDishService.service.DishCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class DishCategoryServiceImpl extends ServiceImpl<DishCategoryMapper, DishCategory> implements DishCategoryService {


    private final DishCategoryMapper dishCategoryMapper;

    private final DishMapper dishMapper;

    /**
     * 查询所有菜品分类
     */
    public List<DishCategoryVO> getCategory() {
        //查询数据库中的所有状态为1的菜品分类
        List<DishCategory> dishCategoryList = dishCategoryMapper.selectList(
                new LambdaQueryWrapper<DishCategory>()
                        .eq(DishCategory::getStatus, StatusConstant.ENABLE)  //1:起售
                        .orderByAsc(DishCategory::getId)
        );

        if(dishCategoryList.isEmpty()) {
            log.info("没有找到启用的菜品分类");
            return new ArrayList<>();  // 返回空集合
        }
        //转化为VO并且返回
        return BeanUtil.copyToList(dishCategoryList, DishCategoryVO.class);
    }

    /**
     * 分类列表查询
     */
    public List<CategoryListVO> getCategoryList(String name) {
        // 1. 构建查询条件
        LambdaQueryWrapper<DishCategory> wrapper = new LambdaQueryWrapper<>();
        if (!StrUtil.isBlank(name)) {
            wrapper.like(DishCategory::getName, name.trim());
        }
        wrapper.orderByDesc(DishCategory::getCreateTime);

        // 2. 查询所有分类
        List<DishCategory> categories = dishCategoryMapper.selectList(wrapper);

        if (categories.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 收集所有分类ID
        List<Long> categoryIds = categories.stream()
                .map(DishCategory::getId)
                .toList();

        // 4. 统计每个分类下的菜品数量（只统计上架菜品）
        List<Dish> dishes = dishMapper.selectList(
                new LambdaQueryWrapper<Dish>()
                        .in(Dish::getCategoryId, categoryIds)
                        .eq(Dish::getStatus, StatusConstant.ENABLE)//1
        );

        Map<Long, Long> dishCountMap = dishes.stream()
                .collect(Collectors.groupingBy(Dish::getCategoryId, Collectors.counting()));

        // 5. 组装返回结果
        List<CategoryListVO> categoryListVOS = BeanUtil.copyToList(categories, CategoryListVO.class);
        categoryListVOS.forEach(categoryListVO -> {categoryListVO.setDishCount(dishCountMap.getOrDefault(categoryListVO.getId(), MessageConstant.ZERO).intValue());});
        return categoryListVOS;
    }

    /**
     * 根据ID查询分类信息
     */
    public CategoryVO getCategoryById(Long id) {
        DishCategory dishCategory = dishCategoryMapper.selectById(id);
        if (dishCategory == null) {
            throw new BusinessException(MessageConstant.NOT_EXIST_CATEGORY);//分类不存在
        }
        return BeanUtil.copyProperties(dishCategory, CategoryVO.class);
    }

    /**
     * 新增分类
     */
    public void addCategory(CategoryAddDTO dto) {
        // 登录校验
        IsLoginUtil.isLogin();

        // 1. 校验分类名称
        String name = dto.getName();
        if (StrUtil.isBlank(name)) {
            throw new BusinessException(MessageConstant.CATEGORY_IS_EMPTY);//菜品分类名称不能为空
        }
        if (name.length() > 10) {
            throw new BusinessException(MessageConstant.NO_USED_NAME);//分类名称不能超过10个字
        }

        // 2. 检查分类名称是否重复
        Long count = dishCategoryMapper.selectCount(
                new LambdaQueryWrapper<DishCategory>()
                        .eq(DishCategory::getName, name.trim())
        );
        if (count > 0) {
            throw new BusinessException(MessageConstant.CATEGORY_EXIST);//菜品分类名称已存在
        }

        // 3. 设置默认值 0 （默认停用）
        DishCategory category = new DishCategory();
        category.setName(name.trim());
        category.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusConstant.DISABLE);

        // 4. 插入数据库
        dishCategoryMapper.insert(category);

        log.info("【新增分类成功：{}】", category.getName());
    }

    /**
     * 修改分类
     */
    public void updateCategory(Long id, CategoryUpdateDTO dto) {
        // 0.登录校验
        IsLoginUtil.isLogin();
        // 1. 根据 id 查询分类是否存在
        DishCategory category = dishCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(MessageConstant.NOT_EXIST_CATEGORY);//菜品分类不存在
        }

        // 2. 校验分类名称
        String name = dto.getName();
        if (StrUtil.isBlank(name)) {
            throw new BusinessException(MessageConstant.CATEGORY_IS_EMPTY);//菜品分类名称不能为空
        }
        if (name.length() > 10) {
            throw new BusinessException(MessageConstant.NO_USED_NAME);//分类名称不能超过10个字
        }

        // 3. 检查分类名称是否与其他分类重复（排除自己）
        Long count = dishCategoryMapper.selectCount(
                new LambdaQueryWrapper<DishCategory>()
                        .eq(DishCategory::getName, name.trim())
                        .ne(DishCategory::getId, id)
        );
        if (count > 0) {
            throw new BusinessException(MessageConstant.CATEGORY_EXIST);//菜品分类名称已存在
        }

        // 4. 更新分类信息
        category.setName(name.trim());
        category.setStatus(dto.getStatus() != null ? dto.getStatus() : category.getStatus());

        dishCategoryMapper.updateById(category);

        log.info("【修改分类成功：id={}, name={}, status={}】", id, category.getName(), category.getStatus());
    }

    /**
     * 分类状态切换
     */
    public Integer toggleCategoryStatus(Long id) {
        // 登录校验
        IsLoginUtil.isLogin();
        // 1. 根据 id 查询分类是否存在
        DishCategory category = dishCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(MessageConstant.NOT_EXIST_CATEGORY);//菜品分类不存在
        }

        // 2. 状态取反
        Integer newStatus = StatusConstant.ENABLE.equals(category.getStatus()) ? StatusConstant.DISABLE : StatusConstant.ENABLE;
        category.setStatus(newStatus);

        // 3. 更新数据库
        dishCategoryMapper.updateById(category);

        log.info("【分类状态切换：id={}, name={}, 新状态={}】", id, category.getName(), newStatus);

        // 4. 返回切换后的状态
        return newStatus;
    }

    /**
     * 删除分类
     */
    public void deleteCategory(Long id) {
        // 登录校验
        IsLoginUtil.isLogin();
        // 1. 根据 id 查询分类是否存在
        DishCategory category = dishCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(MessageConstant.NOT_EXIST_CATEGORY);//菜品分类不存在
        }

        // 2. 检查分类状态：必须停用才能删除
        if (StatusConstant.ENABLE.equals(category.getStatus())) {
            throw new BusinessException(MessageConstant.ENABLE_DISH_CATEGORY);//请先下架菜品分类再删除
        }

        // 3. 检查分类下是否有菜品
        Long dishCount = dishMapper.selectCount(
                new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getCategoryId, id)
        );

        if (dishCount > 0) {
            throw new BusinessException("该分类下有 " + dishCount + " 个菜品，请先删除或转移菜品");
        }

        // 4. 逻辑删除
        dishCategoryMapper.deleteById(category);

        log.info("【删除分类成功： name={}】", category.getName());
    }
}
