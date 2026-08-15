package com.baozi.steamedCartService.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baozi.steamedCommon.domian.dto.CartAddDTO;
import com.baozi.steamedCommon.domian.dto.CartUpdateDTO;
import com.baozi.steamedCartService.entity.Cart;
import com.baozi.steamedCommon.domian.vo.CartVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CartService extends IService<Cart> {


    /**
     * 查询购物车内的菜品
     */
    List<CartVO> getCart(Long cashierId);

    /**
     * 添加购物车菜品
     */
    void add(CartAddDTO cartAddDTO);


    /**
     * 修改购物车内的菜品数量（+1 | -1）
     */
    void update(CartUpdateDTO cartUpdateDTO);

    /**
     * 删除购物车内目标菜品所有数量
     */
    void delete(Long CartId);

    /**
     * 清空购物车
     */
    void clear();
}
