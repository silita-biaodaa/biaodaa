package com.silita.biaodaa.task;

import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersonTask implements Runnable {

    @Autowired
    PersonService personService;
    @Autowired
    MyRedisTemplate myRedisTemplate;

    @Override
    public void run() {
        myRedisTemplate.batchDel(RedisConstantInterface.PERSON_LIST);
        personService.setRedisParam();
    }
}
