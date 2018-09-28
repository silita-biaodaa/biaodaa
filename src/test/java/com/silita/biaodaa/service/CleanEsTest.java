package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.elastic.common.NativeElasticSearchUtils;
import com.silita.biaodaa.es.InitElasticseach;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.Map;

public class CleanEsTest extends ConfigTest{

    @Autowired
    NativeElasticSearchUtils nativeElasticSearchUtils;

    @Test
    public void test(){
        nativeElasticSearchUtils.deleteIndex(InitElasticseach.initClient(),"company");
    }

    /**
     * 获取索引doc数量
     * Elastic Search Java API 2.4
     */
    @Test
    public void getClusterStatus() {

                String response = InitElasticseach.initClient().prepareSearch("company")
                .setSize(0)
                .execute()
                .actionGet()
                .toString();

        Map<String,Object> jsonObject = (Map<String, Object>) JSON.parse(response);
        Map<String,Object> jsonMap = (Map<String, Object>) jsonObject.get("hits");
        String count_num = jsonMap.get("total").toString();

        //System.out.println(JsonResponse.get("hits"));

        System.out.println(count_num);

//        public void delLaw(){
//            QueryBuilder queryBuilder1 = QueryBuilders.rangeQuery("date").gt(new Date().getTime());
//            QueryBuilder queryBuilder3 = QueryBuilders.boolQuery().must(queryBuilder1);
//            SearchRequestBuilder requestBuilder = client.prepareSearch("biaodaa").setTypes("law").setQuery(queryBuilder3).setSize(9000);
//            SearchResponse response = requestBuilder.execute().actionGet();
//            DeleteRequestBuilder deleteRequestBuilder;
//            System.out.println(response.getHits().totalHits);
//            for (SearchHit hit : response.getHits()) {
//                System.out.println(hit.getId());
//                deleteRequestBuilder = client.prepareDelete("biaodaa","law",hit.getId());
//                deleteRequestBuilder.execute().actionGet();
//            }
//        }
    }
}
