package com.baozi.steamedDishService.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baozi.steamedCommon.domian.dto.DishFlavorDTO;
import com.baozi.steamedCommon.domian.vo.DishFlavorVO;
import com.baozi.steamedDishService.entity.DishFlavor;

import java.util.List;

public interface DishFlavorService extends IService<DishFlavor> {

    /**
     * 根据口味ID查询菜品口味
     */
    DishFlavorVO getDishFlavorByFlavorId(Long flavorId);

    /**
     * 查询菜品口味
     */
    DishFlavorVO getDishFlavor(DishFlavorDTO dishFlavorDTO);

    /**
     * 批量查询菜品口味
     */
    List<DishFlavor> listByIds(List<Long> ids);

}
