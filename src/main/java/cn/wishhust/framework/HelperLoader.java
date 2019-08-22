package cn.wishhust.framework;

import cn.wishhust.framework.helper.BeanHelper;
import cn.wishhust.framework.helper.ClassHelper;
import cn.wishhust.framework.helper.ControllerHelper;
import cn.wishhust.framework.helper.IocHelper;
import cn.wishhust.framework.util.ClassUtil;

public final class HelperLoader {

    public static void init() {
        Class<?>[] classList = {
                ClassHelper.class,
                BeanHelper.class,
                IocHelper.class,
                ControllerHelper.class
        };

        for (Class<?> cls : classList) {
            ClassUtil.loadClass(cls.getName());
        }
    }
}
