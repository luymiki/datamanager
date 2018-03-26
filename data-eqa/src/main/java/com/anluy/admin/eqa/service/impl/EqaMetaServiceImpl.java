package com.anluy.admin.eqa.service.impl;

import com.anluy.admin.eqa.entity.EqaMeta;
import com.anluy.admin.eqa.mapper.EqaMetaMapper;
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
public class EqaMetaServiceImpl implements EqaMetaService {
    @Resource
    EqaMetaMapper eqaMetaMapper;

    @Override
    public List<EqaMeta> getAll() {
        return eqaMetaMapper.getAll();
    }
}
