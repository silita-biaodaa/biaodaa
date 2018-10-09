package com.silita.biaodaa.service;

import com.silita.biaodaa.es.InitElasticseach;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class Test extends ConfigTest{

    @Autowired
    LawService lawService;

    private static TransportClient client = InitElasticseach.initClient();

    @org.junit.Test
    public void test() {
        System.out.println(getAll(client,"branch_company","branch_comes"));
    }

    /**
     * 获取总数
     *
     * @param client
     * @param indexName
     * @param type
     * @return
     */
    private Integer getAll(TransportClient client, String indexName, String type) {
        SearchRequestBuilder builder = client.prepareSearch(indexName).setTypes(type);
        SearchResponse response = builder.setQuery(QueryBuilders.matchAllQuery()).execute().actionGet();
        Long hits = response.getHits().getTotalHits();
        Integer count = Integer.valueOf(Long.toString(hits));
        return count;
    }
}
