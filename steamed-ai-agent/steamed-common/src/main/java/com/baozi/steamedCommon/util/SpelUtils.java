package com.baozi.steamedCommon.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SpEL 表达式解析工具类
 */
@Slf4j
public class SpelUtils {

    private static final ParameterNameDiscoverer DISCOVERER = new DefaultParameterNameDiscoverer();
    private static final Pattern NESTED_PATTERN = Pattern.compile("#(\\w+)\\.(\\w+)");
    private static final Pattern SIMPLE_PATTERN = Pattern.compile("#(\\w+)");

    /**
     * 解析 SpEL 表达式
     * @param method 目标方法
     * @param args 方法参数值
     * @param expression 表达式，如 "新增菜品：#dto.name"
     * @return 解析后的字符串
     */
    public static String parse(Method method, Object[] args, String expression) {
        if (!expression.contains("#")) {
            return expression;
        }

        try {
            String[] paramNames = DISCOVERER.getParameterNames(method);
            log.info("参数名：{}", Arrays.toString(paramNames));

            if (paramNames == null || paramNames.length == 0) {
                return expression;
            }

            // 创建上下文，绑定参数
            EvaluationContext context = new StandardEvaluationContext();
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }

            String result = expression;

            // 1. 先处理嵌套属性：#dto.name
            Matcher nestedMatcher = NESTED_PATTERN.matcher(result);
            while (nestedMatcher.find()) {
                String objName = nestedMatcher.group(1);
                String fieldName = nestedMatcher.group(2);
                String placeholder = "#" + objName + "." + fieldName;

                Object obj = context.lookupVariable(objName);
                if (obj != null) {
                    String fieldValue = getFieldValue(obj, fieldName);
                    if (fieldValue != null) {
                        result = result.replace(placeholder, fieldValue);
                    }
                }
            }

            // 2. 再处理简单变量：#name
            Matcher simpleMatcher = SIMPLE_PATTERN.matcher(result);
            while (simpleMatcher.find()) {
                String objName = simpleMatcher.group(1);
                String placeholder = "#" + objName;

                Object obj = context.lookupVariable(objName);
                if (obj != null && !obj.getClass().getName().startsWith("com.baozi")) {
                    // 只替换基本类型和String，避免把DTO对象toString
                    result = result.replace(placeholder, obj.toString());
                }
            }

            return result;

        } catch (Exception e) {
            log.error("SpEL 解析失败", e);
            return expression;
        }
    }

    /**
     * 反射获取字段值
     */
    private static String getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(obj);
            return value != null ? value.toString() : null;
        } catch (NoSuchFieldException e) {
            // 尝试从父类查找
            try {
                Field field = obj.getClass().getField(fieldName);
                Object value = field.get(obj);
                return value != null ? value.toString() : null;
            } catch (Exception ex) {
                log.error("获取字段值失败：{}.{}", obj.getClass().getSimpleName(), fieldName);
                return null;
            }
        } catch (Exception e) {
            log.error("获取字段值失败：{}.{}", obj.getClass().getSimpleName(), fieldName);
            return null;
        }
    }
}