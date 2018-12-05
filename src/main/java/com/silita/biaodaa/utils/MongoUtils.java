package com.silita.biaodaa.utils;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MongoUtils {

    private static Logger logger = LoggerFactory.getLogger(MongoUtils.class);

    public static Datastore init(String ip, String host, String dbName) {
        Map<String, String> serverMap = new HashMap<String, String>() {{
            put(ip, host);
        }};
        List<ServerAddress> serverList = new ArrayList<>();
        if (null != serverMap && !serverMap.isEmpty()) {
            for (Map.Entry<String, String> entry : serverMap.entrySet()) {
                serverList.add(new ServerAddress(entry.getKey(), Integer.parseInt(entry.getValue())));
            }
        }
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.minConnectionsPerHost(10);
        builder.connectionsPerHost(20);
        builder.connectTimeout(1000 * 60);
        builder.socketTimeout(1000 * 60);
        builder.maxWaitTime(1000 * 60);
        builder.threadsAllowedToBlockForConnectionMultiplier(5);
        builder.maxConnectionIdleTime(1000 * 60 * 60);
        builder.maxConnectionLifeTime(1000 * 60 * 60 * 2);
        MongoClient mongoClient = new MongoClient(serverList, builder.build());
        Morphia morphia = new Morphia();
        morphia.mapPackage("com.silita.biaodaa");
        Datastore datastore = morphia.createDatastore(mongoClient, dbName);
        datastore.ensureIndexes();
        logger.info(String.format("MongoDB连接池初始化完毕！主机列表：%s 数据库名：%s", PropertiesUtils.getProperty("mongodb.hosts"), PropertiesUtils.getProperty("mongodb.dbName")));
        return datastore;
    }


}
