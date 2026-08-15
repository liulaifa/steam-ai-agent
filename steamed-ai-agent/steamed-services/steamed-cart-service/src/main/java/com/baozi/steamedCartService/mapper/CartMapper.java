package com.baozi.steamedCartService.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baozi.steamedCartService.entity.Cart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CartMapper extends BaseMapper<Cart> {
}
