package com.anluy.admin.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * 功能说明：银行卡卡bin查询
 * <p>
 * Created by hc.zeng on 2019/1/12.
 */
public class BankBinUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(BankBinUtil.class);
    private static final Map<String, Map> dataMap = new LinkedHashMap<>();
    @Resource
    private JdbcTemplate jdbcTemplate;

    public BankBinUtil(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        init();
    }

    private void init() {
        LOGGER.info("加载银行卡BIN数据。。。。");
        jdbcTemplate.query("select id,bank_name,bank_name_short,card_name,card_length,bin_length,bin,card_type from bank_bin order by bin_length desc,card_length desc", new RowMapper<Map>() {
            @Override
            public Map mapRow(ResultSet resultSet, int i) throws SQLException {
                int count = resultSet.getMetaData().getColumnCount();
                Map<String, Object> map = new HashMap<>();
                for (int j = 1; j <= count; j++) {
                    map.put(resultSet.getMetaData().getColumnLabel(j).toLowerCase(), resultSet.getObject(j));
                }
                dataMap.put((String) map.get("bin"), map);
                return null;
            }
        });
        LOGGER.info("加载银行卡BIN数据完成。。。。");
    }

    /**
     * 根据银行卡号获取卡bin信息
     *
     * @param yhk
     * @return
     */
    public List<Map> getBankBinInfo(String yhk) {
        if (StringUtils.isBlank(yhk)) {
            return null;
        }
        yhk = yhk.trim();
        List<Map> result = new ArrayList();
        int length = yhk.length();
        for (int i = length; i > 1; i--) {
            String kl = yhk.substring(0, i);
            if (dataMap.containsKey(kl)) {
                result.add(dataMap.get(kl));
            }
        }

        return result.isEmpty() ? null : result;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}

