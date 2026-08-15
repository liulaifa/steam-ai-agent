package com.baozi.steamedDishService.util;

import com.baozi.steamedCommon.constant.RedisConstant;
import com.baozi.steamedDishService.entity.Dish;
import com.baozi.steamedDishService.mapper.DishMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 热门菜品 Redis 工具类
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HotDishRedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    private final DishMapper dishMapper;

    /**
     * 更新菜品销量（下单时调用）
     * 对指定菜品的 ZSet Score +1
     */
    public  void incrementSales(Long dishId) {
        redisTemplate.opsForZSet().incrementScore(RedisConstant.HOT_DISH_KEY, String.valueOf(dishId), 1);
    }

    /**
     * 获取热门菜品 Top N（按销量降序）
     * @param topN 获取前几名
     * @return 菜品 ID 的 Set，有序
     */
    public Set<Object> getHotDishIds(Integer topN) {
        return redisTemplate.opsForZSet()
                .reverseRange(RedisConstant.HOT_DISH_KEY, 0, topN - 1);
    }

    /**
     * 获取指定菜品的销量
     */
    public Double getSalesCount(Long dishId) {
        return redisTemplate.opsForZSet().score(RedisConstant.HOT_DISH_KEY, String.valueOf(dishId));
    }
    /**
     * 定时清理：每周日凌晨 3 点清理已被删除的菜品
     */
    @Scheduled(cron = "0 0 3 ? * SUN")
    public void cleanDeletedDishes() {
        log.info("开始清理热门菜品中的无效数据");

        Set<Object> hotDishIds = redisTemplate.opsForZSet()
                .range(RedisConstant.HOT_DISH_KEY, 0, -1);

        if (hotDishIds != null) {
            hotDishIds.forEach(dishId -> {
                Dish dish = dishMapper.selectById(Long.valueOf(dishId.toString()));
                if (dish == null) {
                    redisTemplate.opsForZSet().remove(RedisConstant.HOT_DISH_KEY, dishId);
                    log.info("清理无效菜品：dishId={}", dishId);
                }
            });
        }

        log.info("热门菜品清理完成");
    }
}