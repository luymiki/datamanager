package com.anluy.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.anluy.admin.entity.Suspicious;
import com.anluy.admin.mapper.SuspiciousMapper;
import com.anluy.admin.service.AlUserLocInfoService;
import com.anluy.admin.service.SuspiciousService;
import com.anluy.commons.dao.BaseDAO;
import com.anluy.commons.elasticsearch.ElasticsearchRestClient;
import com.anluy.commons.service.BaseServiceImpl;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * 功能说明：嫌疑人信息管理
 * <p>
 * Created by hc.zeng on 2018/3/22.
 */
@Service
@Transactional
public class AlUserLocInfoServiceImpl extends BaseServiceImpl<String, Suspicious> implements AlUserLocInfoService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private SuspiciousMapper suspiciousMapper;

    @Resource
    private ElasticsearchRestClient elasticsearchRestClient;

    public Object formater(String field, Object value) {
        if (value != null) {
            switch (field) {
                case "create_time":
                case "modify_time": {
                    return DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss");
                }
                case "qq":
                case "weixin":
                case "cft":
                case "zfb":
                case "yhzh":
                case "phone":
                case "imei":
                case "glry": {
                    String[] ss = value.toString().split(" |,|，");
                    return ss;
                }
            }
        }
        return value;
    }

    @Override
    public BaseDAO getBaseDAO() {
        return suspiciousMapper;
    }

    @Override
    public ElasticsearchRestClient getElasticsearchRestClient() {
        return elasticsearchRestClient;
    }

    @Override
    public String getIndexName() {
        return "suspicious";
    }


    @Override
    public List<Map> importSql(String filePath,String backPath,String tags) {
        List<String> stringList = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(filePath)));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringList.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException("文件读取异常", e);
        }
        List<String> relist = jdbcTemplate.query("select * from al_user_loc_infoa", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                StringBuffer buffer = new StringBuffer("INSERT INTO `al_user_loc_infoa` VALUES (");
                int colcount = resultSet.getMetaData().getColumnCount();
                List list = new ArrayList();
                for (int j = 1; j <= colcount; j++) {
                    Object o = resultSet.getObject(j);
                    if (o instanceof java.sql.Timestamp) {
                        o = DateFormatUtils.format(new Date(((java.sql.Timestamp) o).getTime()), "yyyy-MM-dd HH:mm:ss");
                    }
                    list.add(o);
                }
                String str = JSON.toJSONString(list);
                str = str.substring(1);
                str = str.substring(0, str.length() - 1);
                buffer.append(str).append(");");
                return buffer.toString();
            }
        });

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(backPath)));
            for (String str : relist) {
                bufferedWriter.write(str + "\n");
                //System.out.println(str);
            }
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException("生成备份文件异常", e);
        }


        jdbcTemplate.execute("delete from al_user_loc_infoa");

        jdbcTemplate.execute(new StatementCallback<Object>() {
            @Override
            public Object doInStatement(Statement statement) throws SQLException, DataAccessException {
                for (String sql : stringList) {
                    statement.addBatch(sql);
                }
                statement.executeBatch();
                return null;
            }
        });
        String[] tagsArr = tags.split(",");
        //读取新时出具
        List<Map> savelist = jdbcTemplate.query("select * from al_user_loc_infoa", new RowMapper<Map>() {
            @Override
            public Map mapRow(ResultSet resultSet, int i) throws SQLException {
                Map map= new HashMap();
                int colcount = resultSet.getMetaData().getColumnCount();

                for (int j = 1; j <= colcount; j++) {
                    Object o = resultSet.getObject(j);
                    if (o instanceof java.sql.Timestamp) {
                        o = DateFormatUtils.format(new Date(((java.sql.Timestamp) o).getTime()), "yyyy-MM-dd HH:mm:ss");
                    }
                    map.put(resultSet.getMetaData().getColumnName(j).toLowerCase(),o);
                }
                map.put("_id",map.get("id"));
                map.put("tags",tagsArr);
                return map;
            }
        });
        //{"query": {"match_all": {}}}
        //先删除
        String deleteDsl = "{\"query\": { \"match_all\": {}}}";
        elasticsearchRestClient.deleteByQuery(deleteDsl,"xndw_sx");
        //保存数据到es
        elasticsearchRestClient.batchSave(savelist,"xndw_sx");
        return savelist;
    }
}
