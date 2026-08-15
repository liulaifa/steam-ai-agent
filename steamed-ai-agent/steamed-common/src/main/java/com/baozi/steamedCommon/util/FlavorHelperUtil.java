package com.baozi.steamedCommon.util;

import com.baozi.steamedCommon.domian.vo.DishFlavorVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlavorHelperUtil {

    // 根据口味ids将口味数据转换成 Map<Long, List<Integer>>
    public static Map<Long, List<Integer>> getFlavor(List<DishFlavorVO> flavors) {
        Map<Long, List<Integer>> result = new HashMap<>();
        for (DishFlavorVO flavor : flavors) {
            result.put(flavor.getId(), List.of(flavor.getSweet(), flavor.getScallion(), flavor.getCoriander(), flavor.getSpicy()));
        }
        return result;
    }

    // 根据口味id和口味数据构建 DishFlavorVO
    public static DishFlavorVO buildFlavorVO(Long flavorId, List<Integer> flavorData) {

        return DishFlavorVO.builder()
                .id(flavorId)
                .sweet(flavorData.get(0))
                .scallion(flavorData.get(1))
                .coriander(flavorData.get(2))
                .spicy(flavorData.get(3))
                .build();
    }
}
