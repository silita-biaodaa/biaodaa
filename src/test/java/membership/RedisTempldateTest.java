package membership;

import com.silita.biaodaa.service.ConfigTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dh on 2019/1/10.
 */
public class RedisTempldateTest extends ConfigTest {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void testHash(){
        String k = "hi";
//        redisTemplate.opsForHash().scan()
//        System.out.println("operations:"+operations.get("k5"));
//        deleteHashKey(k,"1001||6c0dd9f1c75149799a7d94450e4ed07d");
//        deleteHashKey(k,"1002||6c0dd9f1c75149799a7d94450e4ed07d");
//        Long time2 = System.currentTimeMillis();
//        boolean insertSuccess2 = uniqueInsertToHash(k,"1002||6c0dd9f1c75149799a7d94450e4ed07d",time2);
//        System.out.println("---"+insertSuccess2);
//
//        System.out.println("hasHash:"+hasHash(k,"k10"));
        System.out.println("updateToHash:"+updateToHash(k,"k9",22222L));
    }

    public boolean uniqueInsertToHash(String k,String hk,Long time){
        BoundHashOperations operations = redisTemplate.boundHashOps(k);
        return operations.putIfAbsent(hk,time);
    }

    public boolean updateToHash(String k,String hk,Long time){
        BoundHashOperations operations = redisTemplate.boundHashOps(k);
        if(operations.hasKey(hk)) {
            operations.put(hk, time);
            return true;
        }else{
            return false;
        }
    }

    public boolean hasHash(String k,String hk){
        return redisTemplate.boundHashOps(k).hasKey(hk);
    }

    public void deleteHashKey(String k,String hk){
        redisTemplate.boundHashOps(k).delete(hk);
    }


    @Test
    public void testPutValue(){
        Map t = new HashMap();
        for(int i=0;i<10;i++){
            t.put("k"+i,"123123"+i);
        }
        redisTemplate.opsForHash().putAll("hi",t);
    }
}
