package com.anluy.admin.eqa.mapper;

import com.anluy.admin.eqa.entity.EqaIndex;
import com.anluy.admin.eqa.entity.EqaMeta;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/19.
 */
@Component
public interface EqaIndexMapper {
    @Select("SELECT * FROM Eqa_index order by sort")
    @Results({
            @Result(property = "indexName", column = "index_name"),
            @Result(property = "indexNameCn", column = "index_name_cn"),
            @Result(property = "sort", column = "sort")
    })
    List<EqaIndex> getAll();
}
