package com.silita.biaodaa.utils;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhushuai on 2019/3/7.
 */
public class MongodbUtils {

    private static Logger logger = LoggerFactory.getLogger(MongodbUtils.class);

    public static Datastore init(String ip,String port,String dbName){
        List<ServerAddress> list = new ArrayList<>();
        list.add(new ServerAddress(ip,Integer.valueOf(port)));
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.minConnectionsPerHost(10);
        builder.connectionsPerHost(20);
        builder.connectTimeout(1000 * 60);
        builder.socketTimeout(1000 * 60);
        builder.maxWaitTime(1000 * 60);
        builder.threadsAllowedToBlockForConnectionMultiplier(5);
        builder.maxConnectionIdleTime(1000 * 60 * 60);
        builder.maxConnectionLifeTime(1000 * 60 * 60 * 2);
        MongoClient mongoClient = new MongoClient(list, builder.build());
        Morphia morphia = new Morphia();
        morphia.mapPackage("com.silita.biaodaa");
        Datastore datastore = morphia.createDatastore(mongoClient, dbName);
        datastore.ensureIndexes();
        logger.info(String.format("MongoDB连接池初始化完毕！主机列表：%s 数据库名：%s", PropertiesUtils.getProperty("mongodb.hosts"), PropertiesUtils.getProperty("mongodb.dbName")));
        return datastore;
    }
}
