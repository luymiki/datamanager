package com.anluy.admin.web.neo4j;

import org.neo4j.graphdb.RelationshipType;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2019/2/16.
 */
public interface RelationshipTypes {
    RelationshipType HAVE = RelationshipType.withName("HAVE");
}
