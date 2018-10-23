package com.silita.biaodaa.controller;


import com.silita.biaodaa.common.MyRedisTemplate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
     * @param model
     * @param key
     * @param pwd
     * @param type
     * @return
     */
    @RequestMapping(value = "/clearCache/{key}")
    public String clearCache(Model model, @PathVariable("key") String key, String pwd, String type) {
        try {
            if (pwd != null && pwd.equals("ybjsadmin")) {
                if (type != null && type.equals("batch")) {
                    myRedisTemplate.batchDel(key);
                    model.addAttribute("info", "[key:" + key + "]批量清理成功。。。");
                } else {
                    myRedisTemplate.del(key);
                    model.addAttribute("info", "[key:" + key + "]单条清理成功");
                }
            } else {
                model.addAttribute("info", "request is failure.");
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
        return "jsonView";
    }

}
