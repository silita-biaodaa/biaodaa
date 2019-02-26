package com.silita.biaodaa.task;

import com.silita.biaodaa.service.AuthorizeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dh on 2019/2/25.
 */
@EnableScheduling
@Component
public class HisUserInfoTask {
    private static final Logger logger = Logger.getLogger(HisUserInfoTask.class);

    @Autowired
    AuthorizeService authorizeService;

    @Scheduled(initialDelay  = 30000,fixedRate= 1000*60*60*1 )
    public void batchFixInviteCode(){
        logger.info("####batchFixInviteCode start...");
        int c = authorizeService.queryNullInvitCodeCount();
        while (c>0) {
            int executeCount = 0;
            if(c>20){
                executeCount=20;
            }else{
                executeCount=c;
            }
            List<String> codeList = buildInvitCodes(executeCount);
            authorizeService.batchFixInviteCode(codeList);
            c=c-executeCount;
            logger.debug("剩余处理数："+c);
        }
        logger.info("####batchFixInviteCode:"+c+",finished#####");
    }

    private List<String> buildInvitCodes(int count){
        List<String> invitList = new ArrayList<>(count);
        for(int i=0;i<count;i++){
            invitList.add(authorizeService.constructShareCode());
        }
        return invitList;
    }
}
