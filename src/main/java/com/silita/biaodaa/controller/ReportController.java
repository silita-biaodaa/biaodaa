package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.service.RedisService;
import com.silita.biaodaa.service.ReportService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by zhushuai on 2019/4/16.
 */
@Controller
@RequestMapping("report")
public class ReportController extends BaseController {

    @Autowired
    ReportService reportService;
    @Autowired
    RedisService redisService;

    /**
     * 综合查询并返回查询条件
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public Map<String, Object> queryReport(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        String qualCode = MapUtils.getString(param, "qualCode");
        String code = serQualCode(qualCode);
        param.put("qualCode", StringUtils.strip(code, ","));
        successMsg(resultMap, reportService.saveCondition(param));
        return resultMap;
    }

    /**
     * 综合查询历史记录
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/history/list", method = RequestMethod.POST)
    public Map<String, Object> listHistory(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        param.put("userId", VisitInfoHolder.getUid());
        PageInfo pageInfo = reportService.listHistory(param);
        buildReturnMap(resultMap, pageInfo);
        successMsg(resultMap);
        return resultMap;
    }

    /**
     * 综合查询历史记录
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/send/again", method = RequestMethod.POST)
    public Map<String, Object> againSend(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        boolean isFlag = reportService.updateReportEmail(param);
        if (isFlag){
            redisService.saveRedisMQ(MapUtils.getString(param, "orderNo"));
        }
        successMsg(resultMap);
        return resultMap;
    }
}
