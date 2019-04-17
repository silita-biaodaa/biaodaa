package com.silita.biaodaa.common;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.*;


/**
 * Created by dh on 2017/7/24.
 */
@Component
public class MyRedisTemplate {

    private Logger logger = Logger.getLogger(MyRedisTemplate.class);

    public final static String VIRTUAL_COURSE_PREX = "";

    @Autowired
    private ShardedJedisPool shardedJedisPool;//注入ShardedJedisPool

    public String buildKey(String key){
        return VIRTUAL_COURSE_PREX + key;
    }

    private Set<String> getByPrefix(String prefix) {
        Set<String> setResult = new HashSet<>();
        ShardedJedis jedis = null;
        try {
            jedis =  getJedis();
            Iterator<Jedis> jedisIterator = jedis.getAllShards().iterator();
            while(jedisIterator.hasNext()){
                setResult.addAll(jedisIterator.next().keys(prefix+"*"));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return setResult;
    }

    /**
     * 根据前缀模糊匹配所有key，然后批量删除
     * @param prefix 前缀字符
     */
    public void batchDel(String prefix){
        ShardedJedis jedis= null;
        try {
            Set<String> keys = getByPrefix(prefix);
            if(keys != null && keys.size()>0) {
                jedis = getJedis();
                for (String key : keys) {
                    jedis.del(key);
                }
            }
        }catch (Exception e){
            logger.error("batchDel key error : "+e,e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 删除元素
     * @param key
     */
    public void del(String key){
        ShardedJedis jedis = null;
        try {
            jedis =  getJedis();
            jedis.del(key);
        } catch (Exception e) {
            logger.error("del key error : "+e,e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 设置 String
     * @param key
     * @param value
     */
    public void setString(String key ,String value){
        ShardedJedis jedis = null;
        try {
            jedis =  getJedis();
            jedis.set(buildKey(key),value);
        } catch (Exception e) {
            logger.error("Set key error : "+e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 设置 过期时间
     * @param key
     * @param value
     * @param seconds 以秒为单位
     */
    public void setString(String key ,String value,int seconds){
        ShardedJedis jedis = null;
        try {
            jedis =  getJedis();
            jedis.setex(buildKey(key), seconds, value);
        } catch (Exception e) {
            logger.error("Set keyex error : "+e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     * 获取String值
     * @param key
     * @return value
     */
    public String getString(String key){
        ShardedJedis jedis = null;
        String value = null;
        try {
            jedis = getJedis();
            String bKey = buildKey(key);
            if (jedis == null || !jedis.exists(bKey)) {
                return null;
            }
            value= jedis.get(bKey);
        }catch (Exception e){
            logger.error(e,e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return value;
    }

    public synchronized ShardedJedis getJedis() {
        return shardedJedisPool.getResource();
    }



    public <T> void setList(String key ,List<T> list,int seconds){
        ShardedJedis jedis = null;
        try {
            String bKey = buildKey(key);
            jedis =getJedis();
            jedis.setex(bKey.getBytes(),seconds, ObjectTranscoder.serialize(list));
        } catch (Exception e) {
            logger.error("setList error : "+e);
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public <T> List<T> getList(String key){
        List<T> list = null;
        ShardedJedis jedis = null;
        try {
            String bKey = buildKey(key);
            jedis = getJedis();
            if (jedis == null || !jedis.exists(bKey.getBytes())) {
                return null;
            }
            byte[] in = jedis.get(bKey.getBytes());
            list = (List<T>) ObjectTranscoder.deserialize(in);
        }catch(Exception e){
            logger.error(e,e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return list;
    }



    public void setObject(String key ,Object obj,int seconds){
        ShardedJedis jedis = null;
        try {
            String bKey = buildKey(key);
            jedis =getJedis();
            jedis.setex(bKey.getBytes(),seconds, ObjectTranscoder.serialize(obj));
        } catch (Exception e) {
            logger.error("setObject error : "+e);
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public void setObject(String key ,Object obj){
        ShardedJedis jedis = null;
        try {
            String bKey = buildKey(key);
            jedis =getJedis();
            jedis.set(bKey.getBytes(), ObjectTranscoder.serialize(obj));
        } catch (Exception e) {
            logger.error("setObject error : "+e);
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public Object getObject(String key){
        Object obj = null;
        ShardedJedis jedis = null;
        try {
            String bKey = buildKey(key);
            jedis = getJedis();
            if (jedis == null || !jedis.exists(bKey.getBytes())) {
                return null;
            }
            byte[] in = jedis.get(bKey.getBytes());
            obj = ObjectTranscoder.deserialize(in);
        }catch(Exception e){
            logger.error(e,e);
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return obj;
    }

    /**
     * 设置redis队列（顺序）
     * @param key
     * @param value
     */
    public void lpush(String key,Object value){
        ShardedJedis jedis = null;
        try {
            String bKey = buildKey(key);
            jedis =getJedis();
            jedis.lpush(bKey.getBytes(), ObjectTranscoder.serialize(value));
        } catch (Exception e) {
            logger.error("setObject error : "+e);
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    /**
     *  消费redis队列（）
     * @param key
     * @return
     */
    public Object lpop(String key){
        Object obj = null;
        ShardedJedis jedis = null;
        try {
            String bKey = buildKey(key);
            jedis =getJedis();
            byte[] in = jedis.lpop(bKey.getBytes());
            obj = ObjectTranscoder.deserialize(in);
        } catch (Exception e) {
            logger.error("setObject error : "+e);
        }finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return obj;
    }

    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 获取唯一Id
     * @param key
     * @param hashKey
     * @param delta 增加量（不传采用1）
     * @return
     */
    public Long incrementHash(String key,String hashKey,Long delta){
        try {
            if (null == delta) {
                delta=1L;
            }
            return redisTemplate.opsForHash().increment(key, hashKey, delta);
        } catch (Exception e) {//redis宕机时采用uuid的方式生成唯一id
            logger.error("redis获取唯一Id失败！[key:"+key+"][hashKey:"+hashKey+"]"+e,e);
           return null;
        }
    }

    /**
     * 插入的hashKey唯一，存在则不进行插入。
     * @param k
     * @param hk
     * @param time
     * @return
     */
    public boolean uniqueInsertToHash(String k,String hk,Long time){
        BoundHashOperations operations = redisTemplate.boundHashOps(k);
        return operations.putIfAbsent(hk,time);
    }

    /**
     * 更新hash表中的value,不存在则无操作。
     * @param k
     * @param hk
     * @param time
     * @return true:更新成功
     */
    public boolean updateToHash(String k,String hk,Long time){
        BoundHashOperations operations = redisTemplate.boundHashOps(k);
        if(operations.hasKey(hk)) {
            operations.put(hk, time);
            return true;
        }else{
            return false;
        }
    }

    public void putToHash(String k,String hk,Long time){
        redisTemplate.boundHashOps(k).put(hk, time);
    }

    /**
     * 判断hash表中是否存在hk
     * @param k
     * @param hk
     * @return
     */
    public boolean hasHash(String k,String hk){
        return redisTemplate.boundHashOps(k).hasKey(hk);
    }

    /**
     * 删除一个键值对
     * @param k
     * @param hk
     */
    public void deleteHashKey(String k,String hk){
        redisTemplate.boundHashOps(k).delete(hk);
    }

    /**
     * 获取hash中的值
     * @param k
     * @param hk
     * @return
     */
    public Object getHashValue(String k,String hk){
        return redisTemplate.boundHashOps(k).get(hk);
    }

}
