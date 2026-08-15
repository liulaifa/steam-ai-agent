package com.baozi.steamedDishService.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.domian.dto.DishFlavorDTO;
import com.baozi.steamedCommon.exception.BusinessException;
import com.baozi.steamedCommon.domian.vo.DishFlavorVO;
import com.baozi.steamedDishService.entity.DishFlavor;
import com.baozi.steamedDishService.mapper.DishFlavorMapper;
import com.baozi.steamedDishService.service.DishFlavorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {

    private final DishFlavorMapper dishFlavorMapper;

    /**
     * 根据口味ID查询菜品口味
     */
    @Override
    public DishFlavorVO getDishFlavorByFlavorId(Long flavorId) {
        return BeanUtil.copyProperties(dishFlavorMapper.selectById(flavorId), DishFlavorVO.class);
    }

    /**
     * 查询菜品口味
     */
    public DishFlavorVO getDishFlavor(DishFlavorDTO dto) {
        //判断DTO里面是否有值,无值返回默认口味
        if (dto.getSweet() == null) dto.setSweet(0);
        if (dto.getScallion() == null) dto.setScallion(1);
        if (dto.getCoriander() == null) dto.setCoriander(1);
        if (dto.getSpicy() == null) dto.setSpicy(1);
        //通过DTO里面的字段去匹配数据库中的口味数据
        DishFlavor dishFlavor = dishFlavorMapper.selectOne(
                new LambdaQueryWrapper<DishFlavor>()
                        .eq(DishFlavor::getSweet, dto.getSweet())
                        .eq(DishFlavor::getScallion, dto.getScallion())
                        .eq(DishFlavor::getCoriander, dto.getCoriander())
                        .eq(DishFlavor::getSpicy, dto.getSpicy())
        );

        //查询结果为空
        if (dishFlavor == null) {
            throw new BusinessException(MessageConstant.TRY_AGAIN);
        }
        //转化为VO对象
        return BeanUtil.copyProperties(dishFlavor, DishFlavorVO.class);
    }

    /**
     * 批量查询菜品口味
     */
    @Override
    public List<DishFlavor> listByIds(List<Long> ids) {
        //判断口味ids不为空
        if(ids.isEmpty()){
            return List.of();
        }
        if (ids.stream().anyMatch(id -> id <= 0)){
            throw new BusinessException(MessageConstant.TRY_AGAIN);
        }

        //根据ids查询数据库
        //返回结果
        return dishFlavorMapper.selectBatchIds(ids);
    }


}
