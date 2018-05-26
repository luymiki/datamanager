package com.anluy.admin.eqa.service.impl;

import com.anluy.admin.eqa.entity.EqaIndex;
import com.anluy.admin.eqa.entity.EqaMeta;
import com.anluy.admin.eqa.mapper.EqaIndexMapper;
import com.anluy.admin.eqa.mapper.EqaMetaMapper;
import com.anluy.admin.eqa.service.EqaIndexService;
import com.anluy.admin.eqa.service.EqaMetaService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/26.
 */
@Service
public class EqaIndexServiceImpl implements EqaIndexService {
    @Resource
    EqaIndexMapper eqaIndexMapper;

    @Override
    public List<EqaIndex> getAll() {
        return eqaIndexMapper.getAll();
    }
}
