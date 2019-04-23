package com.silita.biaodaa.service;

import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.ObjectTranscoder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.List;

/**
 * <p>Created by mayongbin01 on 2017/3/9.</p>
 */
public class RedisTest extends ConfigTest {
    @Autowired
    private MyRedisTemplate redisTemplate;
    @Autowired
    private ShardedJedisPool shardedJedisPool;

    @Test
    public void getAndPutTest() {
        ShardedJedis jedis = null;
        try {
            jedis = shardedJedisPool.getResource();
            jedis.lpush("ceshi".getBytes(), ObjectTranscoder.serialize("湖南耀邦"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Test
    public void getList(){
        ShardedJedis jedis = null;
        List<byte[]> list = null;
        try {
            jedis = shardedJedisPool.getResource();
            list = jedis.lrange("ceshi".getBytes(),0,0);
            if(null!= list && list.size() > 0){
                for (byte[] str : list){
                    System.out.println(ObjectTranscoder.deserialize(str));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != jedis) {
                jedis.close();
            }
        }
    }

    @Test
    public void push() {
        String topic = "order_list";
        for (int i = 1; i < 100; i++) {
            redisTemplate.convertAndSend(topic,i+"只猪");
        }
    }

    @Test
    public void pop() {
        String topic = "orderber_list";
        List<String> list = (List<String>) redisTemplate.lpop(topic);
        for (String str : list){
            System.out.println(str);
        }
    }

}