package cn.wishhust.framework.helper;

import cn.wishhust.framework.annotation.Aspect;
import cn.wishhust.framework.annotation.Service;
import cn.wishhust.framework.proxy.AspectProxy;
import cn.wishhust.framework.proxy.Proxy;
import cn.wishhust.framework.proxy.ProxyManager;
import cn.wishhust.framework.proxy.TransactionProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.lang.annotation.Annotation;
import java.util.*;

/**
 * AopHelper需要获取所有目标类及其被拦截到切面类实例，并通过ProxyManager#createProxy方法来创建代理对象，最后将其放入BeanMap中。
 *
 */

public final class AopHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AopHelper.class);

    static {
        try {
            Map<Class<?>, Set<Class<?>>> proxyMap = createProxyMap();
            Map<Class<?>, List<Proxy>> targetMap = createTargetMap(proxyMap);
            for (Map.Entry<Class<?>, List<Proxy>> targetEntry : targetMap.entrySet()) {
                Class<?> targetClass = targetEntry.getKey();
                List<Proxy> proxyList = targetEntry.getValue();
                // 代理类实例
                Object proxy = ProxyManager.createProxy(targetClass, proxyList);
                // 对目标类和代理类实例进行管理
                BeanHelper.setBean(targetClass, proxy);
            }
        } catch (Exception e) {
            LOGGER.error("aop failure", e);
        }
    }


    /**
     * 获取Aspect注解中设置到注解类，若该注解类不是Aspect类，则可调用ClassHelper#getClassSetByAnnotation方法获取相关类，
     * 并把这些类放入集合返回
     * 说明：@Aspect(Controller.class)，表示对所有Controller进行增强（切片）
     * @param aspect
     * @return
     */
    private static Set<Class<?>> createTargetClassSet(Aspect aspect) {
        Set<Class<?>> targetClassSet = new HashSet<>();
        Class<? extends Annotation> annotation = aspect.value();
        // 需要对非切片类进行增强，故!annotation.equals(Aspect.class)
        if (annotation != null && !annotation.equals(Aspect.class)) {
            // 获取应用包名下带有annotation注解的所有类
            targetClassSet.addAll(ClassHelper.getClassSetByAnnotation(annotation));
        }
        return targetClassSet;
    }

    /**
     * 获取代理类（切面类）1     目标类集合n 映射关系
     * @return
     */
    private static Map<Class<?>, Set<Class<?>>> createProxyMap() {
        Map<Class<?>, Set<Class<?>>> proxyMap = new HashMap<>();
        // proxyMap 例如：
        // key   ：cn.wishhust.chapter2.aspect.ControllerAspect
        // value ：0 -> class cn.wishhust.chapter2.controller.CustomerController
        // key   ：cn.wishhust.chapter2.aspect.ServiceAspect
        // value ：0 -> class cn.wishhust.chapter2.controller.CustomerController
        addAspectProxy(proxyMap);
        // key   ：cn.wishhust.framework.proxy.TransactionProxy
        // value ：0 -> cn.wishhust.chapter2.service.CustomerService
        addTransactionProxy(proxyMap);
        return proxyMap;
    }

    /**
     * 获取目标类与代理对象列表映射关系
     * @param proxyMap
     * @return
     * @throws Exception
     */
    private static Map<Class<?>, List<Proxy>> createTargetMap(Map<Class<?>, Set<Class<?>>> proxyMap) throws Exception {
        Map<Class<?>,List<Proxy>> targetMap = new HashMap<>();
        // targetMap 例如：
        // key   ：class cn.wishhust.chapter2.controller.CustomerController
        // value ：0 -> ControllerAspect@6426
        // key   ：class cn.wishhust.chapter2.service.CustomerService
        // value ：0 -> ServiceAspect@5190
        // value ：1 -> TransactionProxy@5091

        for (Map.Entry<Class<?>, Set<Class<?>>> proxyEntry : proxyMap.entrySet()) {
            Class<?> proxyClass = proxyEntry.getKey();
            Set<Class<?>> targetClassSet = proxyEntry.getValue();
            for (Class<?> targetClass : targetClassSet) {
                Proxy proxy = (Proxy) proxyClass.newInstance();
                if (targetMap.containsKey(targetClass)) {
                    targetMap.get(targetClass).add(proxy);
                } else {
                    List<Proxy> proxyList = new ArrayList<>();
                    proxyList.add(proxy);
                    targetMap.put(targetClass, proxyList);
                }
            }
        }
        return targetMap;
    }


    private static void addAspectProxy(Map<Class<?>, Set<Class<?>>> proxyMap) {
        Set<Class<?>> proxyClassSet = ClassHelper.getClassSetBySuper(AspectProxy.class);
        for(Class<?> proxyClass : proxyClassSet) {
            if (proxyClass.isAnnotationPresent(Aspect.class)) {
                Aspect aspect = proxyClass.getAnnotation(Aspect.class);
                Set<Class<?>> targetClassSet = createTargetClassSet(aspect);
                proxyMap.put(proxyClass, targetClassSet);
            }
        }
    }

    private static void addTransactionProxy(Map<Class<?>, Set<Class<?>>> proxyMap) {
        Set<Class<?>> serviceClassSet = ClassHelper.getClassSetByAnnotation(Service.class);
        proxyMap.put(TransactionProxy.class, serviceClassSet);
    }

}
