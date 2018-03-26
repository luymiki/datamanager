package com.anluy.admin.eqa.mapper;

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
public interface EqaMetaMapper  {
    @Select("SELECT * FROM Eqa_Meta")
    @Results({
            @Result(property = "indexName", column = "index_name"),
            @Result(property = "indexNameCn", column = "index_name_cn"),
            @Result(property = "fieldName", column = "field_name"),
            @Result(property = "fieldCode", column = "field_code"),
            @Result(property = "fieldType", column = "field_type")
    })
    List<EqaMeta> getAll();

//    @Select("SELECT * FROM Attachment WHERE id = #{id}")
//    @Results({
//            @Result(property = "createTime", column = "create_time")
//    })
//    Attachment get(String id);
//
//    @Insert("INSERT INTO Attachment(id,folder,tags,path,name,size,suffix,type,create_time,susp_name,susp_id) VALUES(#{id},#{folder},#{tags},#{path},#{name},#{size},#{suffix},#{type},#{createTime},#{suspName},#{suspId})")
//    void save(Attachment attachment);
//
//    @Update("UPDATE Attachment SET folder=#{folder},tags=#{tags},path=#{path},name=#{name},size=#{size},suffix=#{suffix},type=#{type},susp_name=#{suspName},susp_id=#{suspId} WHERE id =#{id}")
//    int update(Attachment attachment);
//
//    @Delete("DELETE FROM Attachment WHERE id =#{id}")
//    int delete(String id);

}
