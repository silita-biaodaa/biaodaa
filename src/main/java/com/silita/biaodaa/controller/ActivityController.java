package com.silita.biaodaa.controller;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.model.TbVipFeeStandard;
import com.silita.biaodaa.service.ActivityService;
import com.silita.biaodaa.service.GiveUserVipService;
import com.silita.biaodaa.service.PageCountService;
import com.silita.biaodaa.service.VipService;
import com.silita.pay.service.OrderInfoService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhushuai on 2019/6/4.
 */
@Controller
@RequestMapping("/activity")
public class ActivityController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(ActivityController.class);
    @Autowired
    private ActivityService activityService;
    @Autowired
    private VipService vipService;
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private GiveUserVipService giveUserVipService;
    @Autowired
    private PageCountService pageCountService;

    @ResponseBody
    @RequestMapping(value = "/save/phone", produces = "application/json;charset=utf-8")
    public Map<String, Object> phoneSave(@RequestBody Map<String, Object> param) {
        Map result = new HashMap();
        successMsg(result);
        if (null == param.get("phone") || "".equals(param.get("phone"))) {
            return result;
        }
        //判断用户是否首充值
        int count = orderInfoService.getUserCount(VisitInfoHolder.getUid());
        if (count > 0) {
            return result;
        }
        TbVipFeeStandard fs = vipService.queryFeeStdInfoByCode(MapUtils.getString(param, "stdCode"));
        param.put("amount", fs.getPrice() * 100);
        param.put("userId", VisitInfoHolder.getUid());
        param.put("tradeType", "NATIVE");
        successMsg(result);
        activityService.saveOrderNo(param);
        return result;
    }

    /**
     * 近期是否有活动
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/entrance", produces = "application/json;charset=utf-8")
    public Map<String, Object> entrance(@RequestBody Map<String, Object> param) {
        Map result = new HashMap();
        try {
            successMsg(result, activityService.isActivity(param));
            return result;
        } catch (Exception e) {
            logger.error("获取活动失败！", e);
            successMsg(result, false);
            return result;
        }
    }

    /**
     * 赠送会员
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/give/user")
    public Map<String, Object> giveVipUser(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        //页面统计
        this.pageCount(request, "/activity/give/user", "点击领取会员接口");
        Map<String, Object> result = new HashedMap(2);
        boolean isFlag = giveUserVipService.giveUserVip(param);
        if (!isFlag) {
            errorMsg(result, Constant.ERR_USER_EXIST, "用户已存在！！！");
            return result;
        }
        successMsg(result);
        return result;
    }

    /**
     * 页面统计
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/page/count")
    public Map<String, Object> pageCount(HttpServletRequest request, @RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashedMap(2);
        String pageUrl = MapUtils.getString(param, "pageUrl");
        String description = MapUtils.getString(param, "description");
        pageCount(request, pageUrl, description);
        successMsg(result);
        return result;
    }

    /**
     * 页面统计
     *
     * @param page        页面url
     * @param description 页面描述
     */
    private void pageCount(HttpServletRequest request, String page, String description) {
        //页面统计
        pageCountService.savePageCount(page, description, request);
    }
}
