package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.service.PrizeService;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 诚信
 * Created by zhushuai on 2019/3/5.
 */

@RequestMapping("/prize")
@Controller
public class PrizeController extends BaseController {


    @Autowired
    PrizeService prizeService;

    /**
     * TODO: 人员详情
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/filter", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> detail() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        resultMap.put("data", prizeService.getPrizeFilter());
        return resultMap;
    }

    /**
     * 获取获奖/不良列表
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> list(@RequestBody Map<String, Object> param) {
        Map<String,Object> resultMap = new HashedMap();
        Map<String,Object> dataMap = new HashedMap();
        PageInfo pageInfo;
        Page page = buildPageIndex(param);
        if (null == param.get("type")) {
            param.put("type", "prize");
        }
        if ("prize".equals(param.get("type"))) {
            pageInfo = prizeService.getPrizeList(page,param);
            buildReturnMap2(dataMap,pageInfo);
            return responseMsg(resultMap,dataMap);
        }
        pageInfo = prizeService.getUndsirableList(page,param);
        buildReturnMap2(dataMap,pageInfo);
        return responseMsg(resultMap,dataMap);
    }
}
