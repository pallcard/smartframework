package cn.wishhust.framework.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回视图对象
 */
public class View {
    /**
     * 视图路径
     */
    private String path;

    /**
     * 模型数据
     */
    private Map<String, Object> model;

    public View(String path) {
        this.path = path;
    }

    public View addModel(String key, Object value) {
        // 判空
        if (model == null) {
            model = new HashMap<>();
        }
        model.put(key, value);
        return this;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public String getPath() {
        return path;
    }
}
