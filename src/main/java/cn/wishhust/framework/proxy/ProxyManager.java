package cn.wishhust.framework.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 代理管理类，提供一个创建代理对象到方法，输入一个目标类和一组Proxy接口
 */
public class ProxyManager {
    /**
     *
     * @param targetClass 目标类
     * @param proxyList Proxy集合
     * @param <T>
     * @return    代理对象
     */
    public static <T> T createProxy(final Class<?> targetClass, final List<Proxy> proxyList) {
        return (T) Enhancer.create(targetClass, new MethodInterceptor() {
            public Object intercept(Object targetObject, Method targetMethod, Object[] methodParams, MethodProxy methodProxy) throws Throwable {

                return new ProxyChain(targetClass, targetObject, targetMethod, methodParams, methodProxy, proxyList).doProxyChain();
            }
        });
    }
}
