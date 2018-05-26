package com.anluy.admin.utils.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/5/17.
 */
public class Node implements BaseNode {
    private int id;
    private String name;
    private List<String> labels = new ArrayList<>();
    private Map<String,Object> property = new HashMap<>();

    @Override
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public List<String> getLabels() {
        return labels;
    }

    @Override
    public Map<String, Object> getPropertis() {
        return property;
    }

    @Override
    public Object getProperty(String key) {
        return property.get(key);
    }
    public void addLabel(String label) {
        this.labels.add(label);
    }
    public void addProperty(String key, Object value) {
        this.property.put(key,value);
    }
}
