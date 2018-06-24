package com.anluy.admin.mapper;

import com.anluy.admin.entity.Attachment;
import com.anluy.commons.dao.BaseDAO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/3/19.
 */
@Component
public interface AttachmentMapper extends BaseDAO<String, Attachment> {
    @Select("SELECT * FROM Attachment")
    @Results({
            @Result(property = "createTime", column = "create_time")
    })
    List<Attachment> getAll();

    @Select("SELECT * FROM Attachment WHERE id = #{id}")
    @Results({
            @Result(property = "createTime", column = "create_time")
    })
    Attachment get(String id);

    @Select("SELECT * FROM Attachment WHERE md5 = #{md5}")
    @Results({
            @Result(property = "createTime", column = "create_time")
    })
    Attachment getMd5(String md5);

    @Insert("INSERT INTO Attachment(id,folder,tags,path,name,size,suffix,type,create_time,susp_name,susp_id,md5) VALUES(#{id},#{folder},#{tags},#{path},#{name},#{size},#{suffix},#{type},#{createTime},#{suspName},#{suspId},#{md5})")
    void save(Attachment attachment);

    @Update("UPDATE Attachment SET folder=#{folder},tags=#{tags},path=#{path},name=#{name},size=#{size},suffix=#{suffix},type=#{type},susp_name=#{suspName},susp_id=#{suspId},md5=#{md5} WHERE id =#{id}")
    int update(Attachment attachment);

    @Delete("DELETE FROM Attachment WHERE id =#{id}")
    int delete(String id);

}
