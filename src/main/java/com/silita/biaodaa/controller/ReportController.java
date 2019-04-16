package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.service.ReportService;
import org.apache.commons.collections.map.HashedMap;
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
        return resultMap;
    }
}
