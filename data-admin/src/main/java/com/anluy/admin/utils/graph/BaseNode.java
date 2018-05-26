package com.anluy.admin.utils.graph;

import java.util.List;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/5/17.
 */
public interface BaseNode {
    int getId();

    String getName();

    List<String> getLabels();

    Map<String, Object> getPropertis();

    Object getProperty(String key);

    void addLabel(String label);

    void addProperty(String key, Object value);
}
