package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.ReputationComputerService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by zhushuai on 2019/3/29.
 */
@Controller
@RequestMapping("/reputation")
public class ReputationController extends BaseController{

    @Autowired
    ReputationComputerService reputationComputerService;

    /**
     * 列表
     *
     * @param param
     */
    @ResponseBody
    @RequestMapping(value = "/company", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> company(@RequestBody Map<String, Object> param) {
        Map<String,Object> result = new HashedMap();
        successMsg(result);
        if (null == param.get("comId")){
            result.put("code",0);
            result.put("msg","参数不允许为空");
            return result;
        }
        successMsg(result,reputationComputerService.computer(param));
        return result;
    }

    /**
     * 列表
     *
     * @param param
     */
    @ResponseBody
    @RequestMapping(value = "/undesirable", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> undesirable(@RequestBody Map<String, Object> param) {
        Map<String,Object> result = new HashedMap();
        successMsg(result);
        if (null == param.get("comId")){
            result.put("code",0);
            result.put("msg","参数不允许为空");
            return result;
        }
        successMsg(result,reputationComputerService.listUndesirable(param));
        return result;
    }

    /**
     * 列表
     *
     * @param param
     */
    @ResponseBody
    @RequestMapping(value = "/new/company", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> companyNew(@RequestBody Map<String, Object> param) {
        Map<String,Object> result = new HashedMap();
        if (null == param.get("comId")){
            result.put("code",0);
            result.put("msg","参数comId不允许为空");
            return result;
        }
        if (null == param.get("reqType")){
            result.put("code",0);
            result.put("msg","参数reqType不允许为空");
            return result;
        }
        successMsg(result,reputationComputerService.listCompanyAward(param));
        return result;
    }
}
