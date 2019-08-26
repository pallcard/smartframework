package cn.wishhust.framework.helper;

import cn.wishhust.framework.annotation.Action;
import cn.wishhust.framework.bean.Handler;
import cn.wishhust.framework.bean.Request;
import cn.wishhust.framework.util.ArrayUtil;
import cn.wishhust.framework.util.CollectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * controller助手类
 *
 * 作用： 将请求和处理对应起来，即生成一个请求与处理器的映射关系（Map）
 * Request 请求（请求方法+路径）
 * Handler 处理对象
 */
public final class ControllerHelper {
    /**
     * 用于存放请求与处理器的映射关系
     */
    private static final Map<Request, Handler> ACTION_MAP = new HashMap<Request, Handler>();

//    Controller中一个例子
//    @Action("get:/customer")
//    public View index(Param param) {
//        List<Customer> customerList = customerService.getCustomerList();
//        return new View("customer.jsp").addModel("customerList", customerList);
//    }

    static {
        // 获取所有Controller类
        Set<Class<?>> controllerClassSet = ClassHelper.getControllerClassSet();
        if (CollectionUtil.isNotEmpty(controllerClassSet)) {
            // 遍历这些Controller类
            for (Class<?> controllerClass : controllerClassSet) {
                // 获取Controller类中定义的方法
                Method[] methods = controllerClass.getDeclaredMethods();
                if (ArrayUtil.isNotEmpty(methods)) {
                    // 遍历Controller类中方法
                    for(Method method : methods) {
                        // 判断是否带有Action注解
                        // @Action("get:/customer")
                        if (method.isAnnotationPresent(Action.class)) {
                            // 从Action注解中获取URL映射规则
                            Action action = method.getAnnotation(Action.class);
                            String mapping = action.value();
                            // 验证URL规则
                            // \w 大小写字母，下划线和数字
                            // + 匹配 1 个或者多个字符
                            // * 匹配 0 个或者多个
                            if (mapping.matches("\\w+:/\\w*")) {
                                String [] array = mapping.split(":");
                                if (ArrayUtil.isNotEmpty(array) && array.length == 2) {
                                    // 获取请求方法与请求路径
                                    String requestMethod = array[0];
                                    String requestPath = array[1];
                                    Request request = new Request(requestMethod, requestPath);
                                    Handler handler = new Handler(controllerClass, method);
                                    ACTION_MAP.put(request, handler);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 获取 Handler
     * @param requestMethod
     * @param requestPath
     * @return
     */
    public static Handler getHandler(String requestMethod, String requestPath) {
        Request request = new Request(requestMethod, requestPath);
        return ACTION_MAP.get(request);
    }
}
