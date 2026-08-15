package com.baozi.steamedDishService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baozi.steamedDishService.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
