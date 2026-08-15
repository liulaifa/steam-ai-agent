package com.baozi.steamedDishService.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.BeanToBeanCopier;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.constant.StatusConstant;
import com.baozi.steamedCommon.domian.dto.AICartAddDTO;
import com.baozi.steamedCommon.domian.dto.DishAddDTO;
import com.baozi.steamedCommon.domian.dto.DishPageDTO;
import com.baozi.steamedCommon.domian.dto.DishUpdateDTO;
import com.baozi.steamedCommon.domian.vo.*;
import com.baozi.steamedCommon.exception.BusinessException;
import com.baozi.steamedDishService.entity.Dish;
import com.baozi.steamedDishService.entity.DishCategory;
import com.baozi.steamedDishService.entity.DishFlavor;
import com.baozi.steamedDishService.mapper.DishCategoryMapper;
import com.baozi.steamedDishService.mapper.DishMapper;
import com.baozi.steamedDishService.service.DishService;
import com.baozi.steamedDishService.util.HotDishRedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    private final DishMapper dishMapper;
    private final DishCategoryMapper dishCategoryMapper;
    private final HotDishRedisUtil hotDishRedisUtil;

    /**
     * 查询菜品根据菜品分类id
     */
    public List<DishVO> getDishesByCategoryId(Integer categoryId) {
        //根据分类id查询数据库中的菜品
        List<Dish> dishList = dishMapper.selectList(
                new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getCategoryId,categoryId)
                        .eq(Dish::getStatus, StatusConstant.ENABLE)//1:起售

        );

        if(dishList.isEmpty()) {
            log.info("没有找到启用的菜品");
            return new ArrayList<>();  // 返回空集合
        }
        //转换为vo
        return BeanUtil.copyToList(dishList, DishVO.class);
    }

    /**
     * 查询菜品信息根据菜品id
     */
    public DishVO getDishById(Integer id) {
        // 1. 根据id查询数据库中的菜品
        Dish dish = dishMapper.selectById(id);

        // 2. 没有的话就抛异常
        if (dish == null) {
            throw new BusinessException(MessageConstant.NO_DISH);//菜品不存在
        }

        // 3. 检查菜品是否上架（可选，看业务需求）
        if (StatusConstant.DISABLE.equals(dish.getStatus())) {
            throw new BusinessException(MessageConstant.NOT_SALE);//菜品已下架
        }

        // 4. 转化为DishVO返回给前端
        return BeanUtil.copyProperties(dish, DishVO.class);
    }

    /**
     * 查询所有菜品
     */
    public List<DishVO> getAll() {
        // 查询所有上架菜品
        List<Dish> dishes = dishMapper.selectList(
                new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getStatus, StatusConstant.ENABLE)
                        .orderByAsc(Dish::getCategoryId)
                        .orderByAsc(Dish::getId)
        );

        if (dishes.isEmpty()) {
            return new ArrayList<>();
        }

        // 转换为 VO
        return BeanUtil.copyToList(dishes, DishVO.class);
    }

    /**
     * 菜品分页查询
     */
    public PageResult<DishPageVO> pageQuery(DishPageDTO dto) {

        // 1. 构建分页对象
        Page<Dish> page = new Page<>(dto.getPage(), dto.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        // 状态筛选
        if (dto.getStatus() != null) {
            wrapper.eq(Dish::getStatus, dto.getStatus());
        }

        // 名称模糊搜索
        if (!StrUtil.isBlank(dto.getName())) {
            wrapper.like(Dish::getName, dto.getName().trim());
        }

        // 排序
        wrapper.orderByAsc(Dish::getCategoryId)
                .orderByAsc(Dish::getId);

        // 3. 执行分页查询
        Page<Dish> dishPage = dishMapper.selectPage(page, wrapper);

        // 4. 如果没数据，返回空结果
        if (dishPage.getRecords().isEmpty()) {
            return PageResult.<DishPageVO>builder()
                    .total(0L)
                    .page(dto.getPage())
                    .pageSize(dto.getPageSize())
                    .pages(0L)
                    .list(new ArrayList<>())
                    .build();
        }

        // 5. 收集所有分类ID，批量查询分类名称
        List<Long> categoryIds = dishPage.getRecords().stream()
                .map(Dish::getCategoryId)
                .distinct()
                .toList();

        Map<Long, String> categoryNameMap = dishCategoryMapper.selectBatchIds(categoryIds)
                .stream()
                .collect(Collectors.toMap(DishCategory::getId, DishCategory::getName));

        // 6. 转换为 VO
        List<DishPageVO> dishCategoryPageVOS = BeanUtil.copyToList(dishPage.getRecords(), DishPageVO.class);
        dishCategoryPageVOS.forEach(
                dishPageVO -> {
                    dishPageVO.setCategoryName(categoryNameMap.getOrDefault(dishPageVO.getId(), "未知分类"));
                }
        );

        // 7. 返回分页结果
        return PageResult.<DishPageVO>builder()
                .total(dishPage.getTotal())
                .page(dto.getPage())
                .pageSize(dto.getPageSize())
                .pages(dishPage.getPages())
                .list(dishCategoryPageVOS)
                .build();
    }

    /**
     * 新增菜品
     */
    public void addDish(DishAddDTO dto) {
        String name =dto.getName();
        Integer price =dto.getPrice();
        Long categoryId = dto.getCategoryId();
        check(name,price,categoryId);

        // 3. 检查菜品名称是否重复（可选）
        Long count = dishMapper.selectCount(
                new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getName, name.trim())
        );
        if (count > 0) {
            throw new BusinessException(MessageConstant.EXIST_DISH_NAME);//菜品名称已存在
        }

        // 4. 设置默认值
        Dish dish = BeanUtil.copyProperties(dto, Dish.class);

        // 5. 插入数据库
        dishMapper.insert(dish);

        log.info("【新增菜品成功：菜品名称：{}】", dish.getName());
    }

    /**
     * 修改菜品
     */
    public void updateDish(Long id, DishUpdateDTO dto) {
        // 1. 根据 id 查询菜品是否存在
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(MessageConstant.NO_DISH);//菜品不存在
        }

        // 2. dto参数验证
        String name =dto.getName();
        Integer price =dto.getPrice();
        Long categoryId = dto.getCategoryId();
        check(name,price,categoryId);

        // 3. 检查菜品名称是否与其他菜品重复（排除自己）
        Long count = dishMapper.selectCount(
                new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getName, name.trim())
                        .ne(Dish::getId, id)  // 排除自己
        );
        if (count > 0) {
            throw new BusinessException(MessageConstant.EXIST_DISH_NAME);//菜品名称已存在
        }

        // 4. 更新菜品信息
        Dish newDish = BeanUtil.copyProperties(dto, Dish.class);
        newDish.setId(id);
        dishMapper.updateById(newDish);

        log.info("【修改菜品成功：id={}, name={}】", id, newDish.getName());
    }

    /**
     * 菜品上下架切换
     */
    public Integer toggleStatus(Long id) {
        // 1. 根据 id 查询菜品是否存在
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(MessageConstant.NO_DISH);//菜品不存在
        }

        // 2. 如果要上架（当前是下架状态），检查所属分类是否启用
        if (StatusConstant.DISABLE.equals(dish.getStatus())) {
            DishCategory category = dishCategoryMapper.selectById(dish.getCategoryId());
            if (category == null || StatusConstant.DISABLE.equals(category.getStatus())) {
                throw new BusinessException(MessageConstant.DISABLE_CATEGORY);//所选分类已停用，请先启用分类
            }
        }

        // 3. 状态取反
        Integer newStatus = StatusConstant.ENABLE.equals(dish.getStatus())  ? StatusConstant.DISABLE : StatusConstant.ENABLE;
        dish.setStatus(newStatus);

        // 4. 更新数据库
        dishMapper.updateById(dish);

        log.info("【菜品状态切换：id={}, name={}, 新状态={}】", id, dish.getName(), newStatus);

        // 5. 返回切换后的状态
        return newStatus;
    }

    /**
     * 删除菜品
     */
    public void deleteDish(Long id) {
        // 1. 根据 id 查询菜品是否存在
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(MessageConstant.NO_DISH);//菜品不存在
        }

        // 2. 检查菜品状态：必须下架才能删除
        if (StatusConstant.ENABLE.equals(dish.getStatus())) {
            throw new BusinessException(MessageConstant.ENABLE_DISH);//请先下架菜品再删除
        }

        // 3. 逻辑删除
        dishMapper.deleteById(dish);

        log.info("【删除菜品成功：id={}, name={}】", id, dish.getName());
    }

    /**
     * 获取热门菜品 Top 10
     * TODO 目前看不懂
     */
    public List<HotDishVO> getHotDishes() {
        Set<Object> hotDishIds = hotDishRedisUtil.getHotDishIds(10);

        if (hotDishIds == null || hotDishIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> dishIdList = hotDishIds.stream()
                .map(obj -> Long.valueOf(obj.toString()))
                .collect(Collectors.toList());

        List<Dish> dishes = dishMapper.selectList(
                new LambdaQueryWrapper<Dish>()
                        .in(Dish::getId, dishIdList)
                        .eq(Dish::getStatus, 1)
        );

        return dishes.stream()
                .map(dish -> {
                    Double salesCount = hotDishRedisUtil.getSalesCount(dish.getId());
                    return HotDishVO.builder()
                            .id(dish.getId())
                            .name(dish.getName())
                            .price(dish.getPrice())
                            .img(dish.getImg())
                            .description(dish.getDescription())
                            .hasFlavor(dish.getHasFlavor())
                            .salesCount(salesCount != null ? salesCount.intValue() : 0)
                            .build();
                })
                .sorted((a, b) -> b.getSalesCount().compareTo(a.getSalesCount()))
                .collect(Collectors.toList());
    }

    /**
     * AI模块的根据菜品名称匹配对应的菜品信息
     */
    @Override
    public DishVO getDishByName(AICartAddDTO aiCartAddDTO) {
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if(StrUtil.isBlank(aiCartAddDTO.getName()))
        {
            throw new BusinessException(MessageConstant.NOT_DISH_NAME);
        }
        wrapper.like(Dish::getName, aiCartAddDTO.getName());
        long count = dishMapper.selectCount(wrapper);
        if (count == 0) {
            throw new BusinessException(MessageConstant.NO_DISH);
        }
        if (count > 1) {
            throw new BusinessException(MessageConstant.DISH_NAME_AMBIGUOUS);
        }
        Dish dish = dishMapper.selectOne(wrapper);
        return BeanUtil.copyProperties(dish, DishVO.class);
    }

    private void check(String dishName,Integer price,Long categoryId){
        // 1. 校验必填字段
        if (StrUtil.isBlank(dishName)) {
            throw new BusinessException(MessageConstant.NOT_DISH_NAME);//菜品名称不能为空
        }
        if (dishName.length() > 10) {
            throw new BusinessException(MessageConstant.NO_USED_NAME);//菜品名称不能超过10个字
        }

        if (categoryId == null) {
            throw new BusinessException(MessageConstant.NOT_CATEGORY_NAME);//菜品分类不能为空
        }

        if (price == null || price <= 0) {
            throw new BusinessException(MessageConstant.NO_USED_PRICCE);//价格必须大于0
        }

        // 2. 检查分类是否存在且启用
        DishCategory category = dishCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(MessageConstant.NOT_EXIST_CATEGORY);//所选分类不存在
        }
        if (category.getStatus() != 1) {
            throw new BusinessException(MessageConstant.DISABLE_CATEGORY);//所选分类已停用，请先启用分类
        }
    }
}
