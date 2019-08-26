package cn.wishhust.framework.helper;

import cn.wishhust.framework.annotation.Inject;
import cn.wishhust.framework.util.ArrayUtil;
import cn.wishhust.framework.util.CollectionUtil;
import cn.wishhust.framework.util.ReflectionUtil;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 依赖注入助手类
 *
 * 作用：为类注入需要依赖的成员
 *
 * 原理：先通过BeanHelper获取所有BeanMap ，遍历所有映射关系（BeanMap），通过反射获取所有成员遍历，判断变量上是否带有Inject注解，
 * 若有，这个从BeanMap中取出Bean实例，并通过反射修改该成员的值。
 *
 */
public final class IocHelper {
    static {
        // 获取所有Bean类与 Bean实例之间的映射关系
        Map<Class<?>, Object> beanMap = BeanHelper.getBeanMap();
        if (CollectionUtil.isNotEmpty(beanMap)) {
            // 遍历Bean Map
            for(Map.Entry<Class<?>, Object> beanEntry : beanMap.entrySet()) {
                // 从BeanMap中获取Bean类与Bean实例
                Class<?> beanClass = beanEntry.getKey();
                Object beanInstance = beanEntry.getValue();
                // 获取Bean类定义的所有成员遍历
                Field[] beanFields = beanClass.getDeclaredFields();
                if (ArrayUtil.isNotEmpty(beanFields)) {
                    // 遍历BeanField
                    for (Field beanField : beanFields) {
                        if (beanField.isAnnotationPresent(Inject.class)) {
                            // 在BeanMap中获取Bean Field对应实例
                            Class<?> beanFieldClass = beanField.getType();
                            Object beanFieldInstance = beanMap.get(beanFieldClass);
                            if (beanFieldInstance != null) {
                                // 通过反射初始化BeanField的值
                                ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
                            }
                        }

                    }
                }
            }
        }
    }
}
