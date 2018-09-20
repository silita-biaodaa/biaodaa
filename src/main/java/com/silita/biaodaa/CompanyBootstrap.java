package com.silita.biaodaa;

import com.silita.biaodaa.task.*;
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
    @Autowired
    ProjectTask projectTask;

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
                    //TODO: 人员缓存
                    String timerPerson = "02:01:00";
                    String endLockPerson = end + " "+timerPerson.trim();
                    long lockTimesPers = dateUtils.dateDiff(start, endLockPerson, "yyyy-MM-dd HH:mm:ss", "m");
//                    scheduler.scheduleAtFixedRate(personTask, 0, lockTimesPers, TimeUnit.MINUTES);
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
                    //TODO:业绩缓存
                    String proTimer = "06:30:00";
                    String proEndLock = end + " "+proTimer.trim();
                    long proLockTimes = dateUtils.dateDiff(start, proEndLock, "yyyy-MM-dd HH:mm:ss", "m");
                    scheduler.scheduleAtFixedRate(projectTask, proLockTimes,61, TimeUnit.MINUTES);
                    logger.info("===========任务启动完成=========");
                } catch (Exception e) {
                     logger.info("任务启动异常", e);
                }
            }
        }
    }
}
