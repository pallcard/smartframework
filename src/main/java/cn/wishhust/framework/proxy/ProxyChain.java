package cn.wishhust.framework.proxy;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * 执行链式代理
 * 链式代理： 可将多个代理通过一条链串起来，一个个去执行，执行顺序取决与添加到链上的先后顺序
 */
public class ProxyChain {

    // 目标类
    private final Class<?> targetClass;
    // 目标对象
    private final Object targetObject;
    // 目标方法
    private final Method targetMethod;
    // 方法代理
    private final MethodProxy methodProxy;
    // 方法参数
    private final Object[] methodParams;
    // 代理列表
    private List<Proxy> proxyList;
    // 代理索引
    private int proxyIndex = 0;

    public ProxyChain(Class<?> targetClass, Object targetObject, Method targetMethod, MethodProxy methodProxy, Object[] methodParams, List<Proxy> proxyList) {
        this.targetClass = targetClass;
        this.targetObject = targetObject;
        this.targetMethod = targetMethod;
        this.methodProxy = methodProxy;
        this.methodParams = methodParams;
        this.proxyList = proxyList;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public Object[] getMethodParams() {
        return methodParams;
    }

    /**
     * proxyIndex为代理
     * @return
     * @throws Throwable
     */
    public Object doProxyChain() throws Throwable {
        Object methodResult;
        if (proxyIndex < proxyList.size()) {
            methodResult = proxyList.get(proxyIndex++).doProxy(this);
        } else {
            methodResult = methodProxy.invokeSuper(targetObject, methodParams);
        }
        return methodResult;
    }
}
