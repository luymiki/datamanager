package com.anluy.admin.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/4/7.
 */
public class MySqlImportAndExport {

    /**
     * 根据属性文件的配置导出指定位置的指定数据库到指定位置
     * @param jdbcTemplate
     * @param exportPath
     * @throws IOException
     */
    public static void export(JdbcTemplate jdbcTemplate, String exportPath) throws IOException {
//        jdbcTemplate.execute(new StatementCallback<Object>() {
//            @Override
//            public Object doInStatement(Statement statement) throws SQLException, DataAccessException {
//                statement.execute("");
////                statement.executeQuery("");
////                statement.addBatch("");
////                statement.executeBatch();
//                return null;
//            }
//        });
    }

    /**
     * 根据属性文件的配置把指定位置的指定文件内容导入到指定的数据库中
     * @throws IOException
     */
    public static void importSql(JdbcTemplate jdbcTemplate,String importPath) throws IOException {
        jdbcTemplate.execute(new StatementCallback<Object>() {
            @Override
            public Object doInStatement(Statement statement) throws SQLException, DataAccessException {
                statement.execute("");
//                statement.executeQuery("");
//                statement.addBatch("");
//                statement.executeBatch();
                return null;
            }
        });
    }
}
