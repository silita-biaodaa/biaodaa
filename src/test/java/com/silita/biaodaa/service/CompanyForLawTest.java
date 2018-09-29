package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.elastic.common.NativeElasticSearchUtils;
import com.silita.biaodaa.es.ElasticseachService;
import com.silita.biaodaa.task.CompanyForLawTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class CompanyForLawTest extends ConfigTest {

    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    ElasticseachService elasticseachService;
    @Autowired
    LawService lawService;
    @Autowired
    NativeElasticSearchUtils nativeElasticSearchUtils;

    @Test
    public void test() {
        Integer count = tbCompanyMapper.queryCompanyCount();
//        List<CompanyEs> comList = new ArrayList<>();
//        if (count > 0) {
        Map<String, Object> param = new HashMap<>();
        param.put("pageSize", 1000);
        //获取pages
        Integer pages = elasticseachService.getPage(count, 1000);
//        Integer pages = 101;
        Integer pers = elasticseachService.getPage(pages, 6);
        CountDownLatch countDownLatch = new CountDownLatch(6);
        for (int i = 1; i < 7; i++) {
            CompanyForLawTask companyForLawTask = new CompanyForLawTask(((i - 1) * pers), (((i - 1) * pers) + pers), tbCompanyMapper,lawService,nativeElasticSearchUtils);
//            CompanyForLawTask companyForLawTask = new CompanyForLawTask(5, 10, tbCompanyMapper,lawService,nativeElasticSearchUtils);
//            companyForLawTask.run();
            Thread t = new Thread(companyForLawTask);
            t.start();
            System.out.println(((i - 1) * pers) + "\t" + (((i - 1) * pers) + pers));
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
