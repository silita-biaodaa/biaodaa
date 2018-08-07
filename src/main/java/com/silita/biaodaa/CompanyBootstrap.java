package com.silita.biaodaa;

import com.silita.biaodaa.task.CompanyTask;
import com.silita.biaodaa.task.CountBidTask;
import com.silita.biaodaa.task.PersonTask;
import com.silita.biaodaa.task.SnatchUrlTask;
import com.silita.biaodaa.utils.MyDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhangxiahui on 18/3/13.
 */
@Component
@Scope("singleton")
public class CompanyBootstrap implements ApplicationListener<ApplicationEvent> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CountBidTask countBidTask;
    @Autowired
    PersonTask personTask;
    @Autowired
    CompanyTask companyTask;
    @Autowired
    SnatchUrlTask snatchUrlTask;

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ContextRefreshedEvent) {
            boolean isRoot = ((ContextRefreshedEvent) event).getApplicationContext().getParent() == null;
            if (isRoot) {
                try {
                    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(9);
                    MyDateUtils dateUtils = new MyDateUtils();
                    String start = dateUtils.getTime(dateUtils.datetimePattern);
                    String end = dateUtils.getDate(dateUtils.getBeforeCurrentDate(1), "yyyy-MM-dd");
//                    String end = dateUtils.getTime("yyyy-MM-dd");
                    String timer = "03:01:00";
                    String endLock = end + " "+timer.trim();
                    long lockTimes = dateUtils.dateDiff(start, endLock, "yyyy-MM-dd HH:mm:ss", "m");
                    //TODO: 统计中标企业
                    scheduler.scheduleAtFixedRate(countBidTask, lockTimes,24 * 60, TimeUnit.MINUTES);
                    logger.info("----------当前时间【"+start+"】，任务执行时间【"+endLock+"】，中间间隔【"+lockTimes+"】分");
                    //TODO: 人员缓存
                    scheduler.scheduleAtFixedRate(personTask, 0, 61, TimeUnit.MINUTES);
                    //TODO: 公司同步Elasticsearch
                    String comTimer = "01:01:00";
                    String comEndLock = end + " "+comTimer.trim();
                    long comLockTimes = dateUtils.dateDiff(start, comEndLock, "yyyy-MM-dd HH:mm:ss", "m");
                    scheduler.scheduleAtFixedRate(companyTask, comLockTimes,24 * 60, TimeUnit.MINUTES);
                    //TODO: 招标同步Elasticsearch
                    String snaTimer = "05:01:00";
                    String snaEndLock = end + " "+snaTimer.trim();
                    long snaLockTimes = dateUtils.dateDiff(start, snaEndLock, "yyyy-MM-dd HH:mm:ss", "m");
                    scheduler.scheduleAtFixedRate(snatchUrlTask, snaLockTimes,24 * 60, TimeUnit.MINUTES);
                    logger.info("===========任务启动完成=========");
                } catch (Exception e) {
                     logger.info("任务启动异常", e);
                }
            }
        }
    }
}
