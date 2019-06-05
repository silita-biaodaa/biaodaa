package com.silita.biaodaa.controller;

import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.model.TbVipFeeStandard;
import com.silita.biaodaa.service.ActivityService;
import com.silita.biaodaa.service.VipService;
import com.silita.pay.service.OrderInfoService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhushuai on 2019/6/4.
 */
@Controller
@RequestMapping("/activity")
public class ActivityController extends BaseController {

    @Autowired
    ActivityService activityService;
    @Autowired
    VipService vipService;
    @Autowired
    OrderInfoService orderInfoService;

    @ResponseBody
    @RequestMapping(value = "/save/phone", produces = "application/json;charset=utf-8")
    public Map<String, Object> phoneSave(@RequestBody Map<String, Object> param) {
        Map result = new HashMap();
        successMsg(result);
        if (null == param.get("phone") || "".equals(param.get("phone"))){
            return result;
        }
        //判断用户是否首充值
        int count = orderInfoService.getUserCount(VisitInfoHolder.getUid());
        if (count > 0){
            return result;
        }
        TbVipFeeStandard fs = vipService.queryFeeStdInfoByCode(MapUtils.getString(param, "stdCode"));
        param.put("amount", fs.getPrice() * 100);
        param.put("userId", VisitInfoHolder.getUid());
        param.put("tradeType","NATIVE");
        successMsg(result);
        activityService.saveOrderNo(param);
        return result;
    }

}
