package com.silita.biaodaa.service;

import com.silita.biaodaa.common.MyRedisTemplate;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Created by mayongbin01 on 2017/3/9.</p>
 */
public class RedisTest extends ConfigTest {
    @Autowired
    private MyRedisTemplate redisTemplate;

    @Test
    public void getAndPutTest() {
        //redisTemplate.opsForHash().put("user", "age", "20");
        Object object = redisTemplate.getObject("test");
        System.out.println(object);
    }






}