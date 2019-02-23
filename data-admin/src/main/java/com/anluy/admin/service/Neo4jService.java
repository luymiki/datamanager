package com.anluy.admin.service;

import java.util.Map;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2019/2/18.
 */
public interface Neo4jService extends TjfxService {
    void createNeo4j(String suspId,String token)throws Exception;
    Map queryCypher(String cypher);
    Map queryCypherPath(String cypher);
    String createCypher(String type, String keyword);
    String createCypherPath(String type, String keyword, String type2, String keyword2);
}
