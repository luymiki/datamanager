package com.anluy.admin;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

/**
 * 功能说明：初始化Neo4j数据库
 * <p>
 * Created by hc.zeng on 2019/2/15.
 */
@Configuration
public class Neo4jConfig {

    @Bean
    public GraphDatabaseService graphDatabaseService(@Qualifier("fileManagerConfig") FileManagerConfig fileManagerConfig) {
        File file = new File(fileManagerConfig.getUploadDir() + "/" + "NEO4J-DATABASE");
        if (!file.exists()) {
            file.mkdirs();
        }
        GraphDatabaseService graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(file)
                .setConfig(GraphDatabaseSettings.pagecache_memory, "512M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig(GraphDatabaseSettings.array_block_size, "300")
                .newGraphDatabase();
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
        return graphDb;
    }
}
