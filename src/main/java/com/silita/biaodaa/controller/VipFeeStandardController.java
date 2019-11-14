package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.service.TbVipFeeStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/standard")
public class VipFeeStandardController extends BaseController{
    @Autowired
    private TbVipFeeStandardService tbVipFeeStandardService;

    /**
     * 按类型获取所有价格
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getvipFeeStandard", produces = "application/json;charset=utf-8")
    public Map<String, Object> getvipFeeStandard(@RequestBody Map param) {
        return successMsgs(tbVipFeeStandardService.getVipFeeStandard(param));
    }
}
