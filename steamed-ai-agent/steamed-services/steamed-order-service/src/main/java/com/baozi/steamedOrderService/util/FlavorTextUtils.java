package com.baozi.steamedOrderService.util;


import com.baozi.steamedCommon.domian.vo.DishFlavorVO;

/**
 * 口味文字工具类
 */
public class FlavorTextUtils {

    /**
     * 根据口味实体构建口味文字描述
     */
    public static String build(DishFlavorVO flavor) {
        if (flavor == null) {
            return "默认口味";
        }

        StringBuilder sb = new StringBuilder();

        sb.append(flavor.getSweet() == 1 ? "加甜" : "不加甜");
        sb.append(flavor.getScallion() == 1 ? "，加葱" : "，不加葱");
        sb.append(flavor.getCoriander() == 1 ? "，加香菜" : "，不加香菜");

        switch (flavor.getSpicy()) {
            case 0: sb.append("，不辣"); break;
            case 1: sb.append("，微辣"); break;
            case 2: sb.append("，中辣"); break;
            case 3: sb.append("，特辣"); break;
            default: sb.append("，不辣");
        }

        return sb.toString();
    }
}
