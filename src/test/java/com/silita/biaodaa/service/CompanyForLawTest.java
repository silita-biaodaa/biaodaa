package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.elastic.common.NativeElasticSearchUtils;
import com.silita.biaodaa.es.ElasticseachService;
import org.springframework.beans.factory.annotation.Autowired;

public class CompanyForLawTest extends ConfigTest {

    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    ElasticseachService elasticseachService;
    @Autowired
    LawService lawService;
    @Autowired
    NativeElasticSearchUtils nativeElasticSearchUtils;

}
