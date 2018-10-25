package com.silita.biaodaa.controller;


import com.silita.biaodaa.common.MyRedisTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dh on 2017/9/5.
 */
@Controller
@RequestMapping("/redis")
public class RedisController {
    private Log logger = LogFactory.getLog(RedisController.class);

    @Autowired
    private MyRedisTemplate myRedisTemplate;

    /**
     * 清除缓存
     *
     * @param key
     * @param pwd
     * @param type
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/clearCache/{key}",produces = "application/json;charset=utf-8")
    public Map<String, Object> clearCache(@PathVariable("key") String key, String pwd, String type) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 1);
        try {
            if (pwd != null && pwd.equals("ybjsadmin")) {
                if (type != null && type.equals("batch")) {
                    myRedisTemplate.batchDel(key);
                    resultMap.put("msg", "[key:" + key + "]批量清理成功。。。");
                    return resultMap;
                }
                myRedisTemplate.del(key);
                resultMap.put("msg", "[key:" + key + "]单条清理成功");
                return resultMap;
            }
            resultMap.put("code", 0);
            resultMap.put("msg", "request is failure.");
            return resultMap;
        } catch (Exception e) {
            logger.error(e, e);
            resultMap.put("code", 0);
            resultMap.put("msg", "request is failure.");
        }
        return null;
    }

}
