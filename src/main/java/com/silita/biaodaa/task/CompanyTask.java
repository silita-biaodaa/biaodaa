package com.silita.biaodaa.task;

import com.silita.biaodaa.es.ElasticseachService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CompanyTask implements Runnable {

    private Logger logger = LoggerFactory.getLogger(CompanyTask.class);

    @Autowired
    ElasticseachService elasticseachService;

    @Override
    public void run() {
        try {
            logger.info("--------------公司同步elasticsearch开始");
            elasticseachService.bastchAddCompany();
            logger.info("--------------公司同步elasticsearch结束");
        }catch (Exception e){
            logger.error("----------------公司同步elasticsearch失败");
        }
    }
}
