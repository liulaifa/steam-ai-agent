package com.baozi.steamedDishService.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baozi.steamedCommon.domian.dto.AICartAddDTO;
import com.baozi.steamedCommon.domian.dto.DishAddDTO;
import com.baozi.steamedCommon.domian.dto.DishPageDTO;
import com.baozi.steamedCommon.domian.dto.DishUpdateDTO;
import com.baozi.steamedCommon.domian.vo.DishPageVO;
import com.baozi.steamedCommon.domian.vo.DishVO;
import com.baozi.steamedCommon.domian.vo.HotDishVO;
import com.baozi.steamedCommon.domian.vo.PageResult;
import com.baozi.steamedDishService.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {
    /**
     * 查询菜品根据菜品分类id
     * @param categoryId
     * @return
     */
    List<DishVO> getDishesByCategoryId(Integer categoryId);

    /**
     * 查询菜品根据菜品id
     * @param id
     * @return
     */
    DishVO getDishById(Integer id);

    /**
     * 查询所有菜品
     * @return
     */
    List<DishVO> getAll();


    /**
     * 菜品分页查询
     * @param dto
     * @return
     */
    PageResult<DishPageVO> pageQuery(DishPageDTO dto);

    /**
     * 新增菜品
     */
    void addDish(DishAddDTO dto);

    /**
     * 修改菜品
     */
    void updateDish(Long id, DishUpdateDTO dto);


    /**
     * 菜品上下架切换
     */
    Integer toggleStatus(Long id);

    /**
     * 删除菜品
     */
    void deleteDish(Long id);

    /**
     * 获取热门菜品 Top 10
     */
    List<HotDishVO> getHotDishes();

    /**
     * AI模块的根据菜品名称匹配对应的菜品信息
     */
    DishVO getDishByName(AICartAddDTO aiCartAddDTO);
}
