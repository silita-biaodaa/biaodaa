package com.silita.biaodaa.service;

import com.silita.biaodaa.elastic.common.NativeElasticSearchUtils;
import com.silita.biaodaa.es.InitElasticseach;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CleanEsTest extends ConfigTest{

    @Autowired
    NativeElasticSearchUtils nativeElasticSearchUtils;

    @Test
    public void test(){
        nativeElasticSearchUtils.deleteIndex(InitElasticseach.initClient(),"company");
    }
}
