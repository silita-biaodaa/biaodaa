package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 人员
 */
@RestController
@RequestMapping("/person")
public class PersonController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    PersonService personService;

    /**
     * TODO: 人员详情
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> detail(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        resultMap.put("data", personService.getPersonDetail(param));
        return resultMap;
    }

    /**
     * TODO: 手动执行人员缓存
     *
     * @param param
     * @return
     */
    @RequestMapping(value = "/addRedis", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> addRedis(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        logger.info("---------手动执行缓存人员数据begin------------");
        personService.setRedisParam();
        logger.info("---------手动执行缓存人员数据end------------");
        return resultMap;
    }
}
