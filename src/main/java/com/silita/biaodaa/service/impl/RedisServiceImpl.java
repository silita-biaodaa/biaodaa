package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.service.RedisService;
import com.silita.biaodaa.utils.MyStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import static com.silita.biaodaa.common.RedisConstantInterface.ORDER_LIST;
import static com.silita.biaodaa.common.RedisConstantInterface.ORDER_LIST_ZHUANCHA;

/**
 * Created by zhushuai on 2019/4/17.
 */
@Service("redisService")
public class RedisServiceImpl implements RedisService {

    @Autowired
    MyRedisTemplate myRedisTemplate;




    @Override
    public void saveRedisMQ(String orderNo,String type) {
        if("zhuancha".equals(type)){
            myRedisTemplate.lpushs(ORDER_LIST_ZHUANCHA,orderNo);
        }else{
            myRedisTemplate.lpush(ORDER_LIST,orderNo);
        }
    }



    @Override
    public String customerOrder(String key) {
        Object object = myRedisTemplate.lpop(ORDER_LIST);
        if (MyStringUtils.isNotNull(object)){
            return object.toString();
        }
        return null;
    }
}
