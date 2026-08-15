package com.baozi.steamedCartService.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baozi.steamedApi.client.DishClient;
import com.baozi.steamedCartService.mapper.CartMapper;
import com.baozi.steamedCartService.service.CartService;
import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.context.CashierContext;
import com.baozi.steamedCommon.domian.dto.CartAddDTO;
import com.baozi.steamedCommon.domian.dto.CartUpdateDTO;
import com.baozi.steamedCartService.entity.Cart;
import com.baozi.steamedCommon.domian.vo.DishVO;
import com.baozi.steamedCommon.exception.BusinessException;
import com.baozi.steamedCommon.domian.vo.CartVO;
import com.baozi.steamedCommon.domian.vo.DishFlavorVO;
import com.baozi.steamedCommon.util.FlavorHelperUtil;
import com.baozi.steamedCommon.util.IsLoginUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final CartMapper cartMapper;
    private final DishClient dishClient;

    /**
     * 查询购物车内的菜品
     */
    @Transactional(rollbackFor = Exception.class)
    public List<CartVO> getCart(Long cashierId) {
        //登录校验
        IsLoginUtil.isLogin(cashierId);

        //通过前台id去查询数据库中的购物车内的所有菜品
        List<Cart> cartDishes = cartMapper.selectList(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getCashierId, cashierId)
                        .orderByDesc(Cart::getCreateTime)
        );

        // 如果购物车为空，返回空集合
        if (cartDishes.isEmpty()) {
            return new ArrayList<>();
        }

        // 3. 收集需要查询口味的 flavorIDs（只查 has_flavor=1 的）
        List<Long> flavorIds = cartDishes.stream()
                .filter(dish -> dish.getHasFlavor() == 1)
                .map(Cart::getFlavorId)
                .distinct()
                .toList();
        //4. 根据FlavorIDs查询到对应的口味数据集合
        List<DishFlavorVO> flavors = dishClient.getFlavorsByIds(flavorIds).getData();

        //5. 将flavorId和口味数据封装到 Map<Long, List<Integer>> ---> id 口味数据
        Map<Long, List<Integer>> flavorMap = FlavorHelperUtil.getFlavor(flavors);
        //5. 构建cartVo(cart（已有数据） + flavorId->DishFlavorVO)
        List<CartVO> cartVO = BeanUtil.copyToList(cartDishes, CartVO.class);
        cartVO.forEach(cart -> {
            if (cart.getHasFlavor() == 1 && cart.getFlavorId() != null) {
                List<Integer> flavorData = flavorMap.get(cart.getFlavorId());
                if (flavorData != null) {
                    cart.setDishFlavorVO(FlavorHelperUtil.buildFlavorVO(cart.getFlavorId(), flavorData));
                }
            }
        });
        return cartVO;
    }

    /**
     * 添加购物车菜品
     */
    @Transactional(rollbackFor = Exception.class)
    public void add(CartAddDTO cartAddDTO) {
        // 1. 获取当前收银员ID并进行登录校验
        Long cashierId = CashierContext.getCurrentId();
        IsLoginUtil.isLogin(cashierId);

        // 2. 查询菜品
        DishVO dishVO = dishClient.getDishById (cartAddDTO.getDishId()).getData();
        if (dishVO == null) {
            throw new BusinessException(MessageConstant.NO_DISH);//菜品不存在
        }
        if (dishVO.getStatus() != 1) {
            throw new BusinessException(MessageConstant.NOT_SALE);//菜品已下架
        }

        // 3. 处理 flavorId 为空就给默认值1L
        long flavorId = cartAddDTO.getFlavorId() != null ? cartAddDTO.getFlavorId() : 1L;

        // 4. 检查购物车是否已存在相同菜品+口味
        Cart existingCart = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getCashierId, cashierId)
                        .eq(Cart::getDishId, cartAddDTO.getDishId())
                        .eq(Cart::getFlavorId, flavorId)
        );

        if (existingCart != null) {
            // 已存在，购物车内菜品数量+1
            existingCart.setNumber(existingCart.getNumber() + 1);
            cartMapper.updateById(existingCart);
            log.info("【购物车数量+1：菜品名称={}, 菜品口味={}, 新数量={}】",
                     existingCart.getDishName(), dishClient.getDishFlavorByFlavorId(cartAddDTO.getFlavorId()), existingCart.getNumber());
        } else {
            // 不存在，新增菜品至购物车
            Cart cart = new Cart();
            cart.setCashierId(cashierId);
            cart.setDishId(dishVO.getId());
            cart.setDishName(dishVO.getName());
            cart.setFlavorId(flavorId);
            cart.setHasFlavor(dishVO.getHasFlavor());
            cart.setPrice(dishVO.getPrice());
            cart.setNumber(1);
            cartMapper.insert(cart);
            log.info("【新增购物车记录：菜品名称={}, 菜品口味={}】", cart.getDishName(), dishClient.getDishFlavorByFlavorId(cartAddDTO.getFlavorId()));
        }
    }


    /**
     * 修改购物车内的菜品数量（+1 | -1）
     */
    public void update(CartUpdateDTO cartUpdateDTO) {
        // 1. 获取当前收银员ID并校验
        Long cashierId = CashierContext.getCurrentId();
        IsLoginUtil.isLogin(cashierId);

        // 2. 处理 flavorId 默认值
        Long flavorId = cartUpdateDTO.getFlavorId() != null ? cartUpdateDTO.getFlavorId() : 1L;

        // 3. 查询购物车记录
        Cart cart = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getCashierId, cashierId)
                        .eq(Cart::getDishId, cartUpdateDTO.getDishId())
                        .eq(Cart::getFlavorId, flavorId)
        );

        if (cart == null) {
            throw new BusinessException(MessageConstant.NO_DISH);//菜品不存在
        }

        // 4. 计算新数量
        int newNumber = cart.getNumber() + cartUpdateDTO.getNumber();

        // 5. 根据新数量决定更新还是删除
        if (newNumber > 0) {
            //5.1.更新
            cart.setNumber(newNumber);
            cartMapper.updateById(cart);
            log.info("【更新购物车内目标菜品数量：菜品名称={}, 新数量={}】",
                    cart.getDishName(), cart.getNumber());
        } else {
            //5.2. 删除
            cartMapper.deleteById(cart.getId());
            log.info("【菜品数量减少后数量小于0，删除购物车目标菜品记录：菜品名称={}】",
                    cart.getDishName());
        }
    }

    /**
     * 删除购物车内目标菜品所有数量
     */
    public void delete(Long CartId) {
        // 1. 获取当前收银员ID并校验
        Long cashierId = CashierContext.getCurrentId();
        IsLoginUtil.isLogin(cashierId);

        // 2. 查询购物车内目标菜品记录（校验是否属于当前收银员）
        Cart cart = cartMapper.selectOne(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getId, CartId)
                        .eq(Cart::getCashierId, cashierId)
        );

        if (cart == null) {
            throw new BusinessException(MessageConstant.USERNAME_NOT_MATCH);//账号不匹配
        }

        // 3. 删除
        cartMapper.deleteById(CartId);
        log.info("【删除购物车内目标菜品记录：dishName={}】", cart.getDishName());

    }

    /**
     * 清空购物车
     */
    public void clear() {
        // 1. 获取当前收银员ID并校验
        Long cashierId = CashierContext.getCurrentId();
        IsLoginUtil.isLogin(cashierId);

        // 2. 删除该收银员的所有购物车记录
        int deletedCount = cartMapper.delete(
                new LambdaQueryWrapper<Cart>()
                        .eq(Cart::getCashierId, cashierId)
        );

        log.info("【清空购物车：cashierId={}, 删除记录数={}】", cashierId, deletedCount);
    }

}
