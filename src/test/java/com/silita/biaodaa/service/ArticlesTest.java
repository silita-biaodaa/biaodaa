package com.silita.biaodaa.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.silita.biaodaa.dao.ArticlesMapper;
import com.silita.biaodaa.utils.MongodbUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.map.HashedMap;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Created by zhushuai on 2019/10/17.
 */
public class ArticlesTest extends ConfigTest {

    @Autowired
    ArticlesMapper articlesMapper;

    private static Datastore db = MongodbUtils.init(PropertiesUtils.getProperty("mongodb.ip"), PropertiesUtils.getProperty("mongodb.port"), "tb_all");

    @org.junit.Test
    public void test() {
        DBCollection dbCollection = db.getDB().getCollection("jst");
        int total = dbCollection.find(new BasicDBObject("province", "湖南")).count();
        if (total <= 0) {
            return;
        }
        Map articles;
        DBCursor dbCursor = dbCollection.find(new BasicDBObject("province", "湖南"));
        while (dbCursor.hasNext()) {
            DBObject dbObject = dbCursor.next();
            Map map = dbObject.toMap();
            articles = new HashedMap(4){{
                put("time",map.get("date"));
                put("title",map.get("title"));
                put("content",map.get("content"));
                put("url",map.get("url"));
            }};
            articlesMapper.insert(articles);
        }
    }

}
