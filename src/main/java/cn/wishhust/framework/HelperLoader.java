package cn.wishhust.framework;

import cn.wishhust.framework.helper.*;
import cn.wishhust.framework.util.ClassUtil;

public final class HelperLoader {

    public static void init() {
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                AopHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };

        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName());
        }
    }
}
