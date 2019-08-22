package cn.wishhust.framework.bean;

import java.util.Map;

/**
 * 返回视图对象
 */
public class View {
    private String path;

    private Map<String, Object> model;

    public View(String path) {
        this.path = path;
    }

    public View addModel(String key, Object value) {
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
