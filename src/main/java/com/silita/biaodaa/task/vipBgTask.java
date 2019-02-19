package com.silita.biaodaa.task;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by dh on 2019/2/19.
 */
@EnableScheduling
public class vipBgTask {

    @Scheduled(cron = "30 10 0 * * * *")//每天凌晨执行
    public void vipExpiredCheck(){

    }
}
