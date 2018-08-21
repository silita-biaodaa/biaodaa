package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.elastic.common.NativeElasticSearchUtils;
import com.silita.biaodaa.es.InitElasticseach;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    }
}
