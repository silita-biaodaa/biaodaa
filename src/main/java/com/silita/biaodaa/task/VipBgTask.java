package com.silita.biaodaa.task;

import com.silita.biaodaa.controller.CompanyController;
import com.silita.biaodaa.dao.UserTempBddMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by dh on 2019/2/19.
 */
@EnableScheduling
@Component
public class VipBgTask {
    private static final Logger logger = Logger.getLogger(VipBgTask.class);

    @Autowired
    UserTempBddMapper userTempBddMapper;

    @Scheduled(cron = "10 0 0 * * *")//每天凌晨执行
    public void vipExpiredCheck(){
        logger.info("####vipExpiredCheck start...");
        int count = userTempBddMapper.updateRoleByTimeOutVip();
        logger.info("####vipExpiredCheck:"+count+",finished#####");
    }
}
