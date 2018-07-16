package com.silita.biaodaa.task;

import com.silita.biaodaa.es.ElasticseachService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SnatchUrlTask implements Runnable {

    private Logger logger = LoggerFactory.getLogger(SnatchUrlTask.class);

    @Autowired
    ElasticseachService elasticseachService;

    @Override
    public void run() {
        try {
            logger.info("--------------招标公告同步elasticsearch开始");
            elasticseachService.batchAddSnatchUrl();
            logger.info("--------------招标公告同步elasticsearch结束");
        }catch (Exception e){
            logger.error("----------------招标公告同步elasticsearch失败");
        }
    }
}
