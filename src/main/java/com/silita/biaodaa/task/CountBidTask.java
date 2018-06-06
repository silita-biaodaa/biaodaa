package com.silita.biaodaa.task;

import com.silita.biaodaa.service.CountBidInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CountBidTask implements Runnable {

    private Logger logger = LoggerFactory.getLogger(CountBidTask.class);

    @Autowired
    CountBidInfoService countBidInfoService;

    @Override
    public void run() {
        try {
            logger.info("------------定时任务开始执行------------");
            countBidInfoService.timerCount();
            logger.info("------------定时任务结束执行------------");
        }catch (Exception e){
            logger.error("定时任务执行失败",e);
        }
    }
}
