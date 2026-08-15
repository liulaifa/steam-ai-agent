package com.baozi.steamedAiService.tools;

import cn.hutool.core.bean.BeanUtil;
import com.baozi.steamedAiService.domain.entity.AIOrderAdd;
import com.baozi.steamedApi.client.CartClient;
import com.baozi.steamedApi.client.DishClient;
import com.baozi.steamedApi.client.OrderClient;
import com.baozi.steamedCommon.constant.MessageConstant;
import com.baozi.steamedCommon.context.CashierContext;
import com.baozi.steamedCommon.domian.dto.CartAddDTO;
import com.baozi.steamedCommon.domian.dto.DishFlavorDTO;
import com.baozi.steamedCommon.domian.dto.OrderAddDTO;
import com.baozi.steamedCommon.domian.vo.CartVO;

import com.baozi.steamedCommon.domian.dto.AICartAddDTO;
import com.baozi.steamedCommon.domian.vo.DishFlavorVO;
import com.baozi.steamedCommon.domian.vo.DishVO;
import com.baozi.steamedAiService.domain.entity.AICartAdd;
import com.baozi.steamedCommon.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
//让AI模块可以调用购物车模块的一些方法
public class CartTools {

    private final CartClient cartClient;
    private final DishClient dishClient;
    private final OrderClient orderClient;

    @Tool(description = "查询购物车内菜品")
    public List<CartVO> getCart(){
        Long cashierId = CashierContext.getCurrentId();
        if (cashierId == null) {
            return List.of();
        }
        return cartClient.getCart(cashierId).getData();
    }


    @Tool(description = "根据要添加的菜品信息添加菜品至购物车")
    public void addCart(@ToolParam(description = "要添加菜品的信息") AICartAdd aiCartAdd){
        //调用菜品模块的获取菜品信息方法，拿到菜品信息
        //数据转换，将AICartAdd（本模块的实体类）转换为AICartAddDTO（common模块的DTO）
        AICartAddDTO aiCartAddDTO = BeanUtil.copyProperties(aiCartAdd, AICartAddDTO.class);
        //调用菜品模块的获取菜品信息方法
        DishVO dish = dishClient.getDishByName(aiCartAddDTO);
        //调用菜品模块的获取菜品口味id信息
        DishFlavorVO dishFlavorVO = dishClient.getDishFlavor(
                DishFlavorDTO.builder()
                        .sweet(aiCartAdd.getSweet())
                        .scallion(aiCartAdd.getScallion())
                        .coriander(aiCartAdd.getCoriander())
                        .spicy(aiCartAdd.getSpicy())
                        .build()
        ).getData();
        //根据菜品信息调用购物车的添加菜品模块接口
        cartClient.addCart(
                CartAddDTO.builder()
                .dishId(dish.getId())
                .flavorId(dishFlavorVO.getId())
                .build()
        );
        log.info("【AI添加菜品至购物车成功,菜品名称：{}，菜品口味：{}】", dish.getName(), dishFlavorVO);
    }

    @Tool(description = "清空购物车")
    public void deleteCart(){
        Long cashierId = CashierContext.getCurrentId();
        if (cashierId == null) {
            throw new BusinessException(MessageConstant.NOT_LOGIN);
        }
        cartClient.deleteCart(cashierId);
        log.info("【购物车已清空！】");
    }

    @Tool(description = "将购物车内的所有菜品创建订单下单同时要确定就餐方式、备注（可选）")
    public void createOrder(@ToolParam(description = "要确定就餐方式、备注（可选）信息") AIOrderAdd aiOrderAdd){
        //组装出OrderAddDTO
        OrderAddDTO orderAddDTO = BeanUtil.copyProperties(aiOrderAdd, OrderAddDTO.class);
        orderClient.createOrder(orderAddDTO);
    }


}
