package com.anluy.admin.service;

import com.anluy.admin.entity.Suspicious;
import com.anluy.commons.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
public interface AlUserLocInfoService extends BaseService<String,Suspicious> {

    List<Map> importSql(String importPath, String backPath,String tags);
}
