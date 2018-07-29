package com.silita.biaodaa.service;

import com.silita.biaodaa.common.MyRedisTemplate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.silita.biaodaa.common.RedisConstantInterface.COM_OVER_TIME;

/**
 * <p>Created by mayongbin01 on 2017/3/9.</p>
 */
public class RedisTest extends ConfigTest {
    @Autowired
    private MyRedisTemplate redisTemplate;

    @Test
    public void getAndPutTest() {
        redisTemplate.setObject("111111","1232321dsdssds1",COM_OVER_TIME);
        //redisTemplate.opsForHash().put("user", "age", "20");
        Object object = redisTemplate.getObject("111111");
        System.out.println(object);
    }






}