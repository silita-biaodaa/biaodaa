package com.silita.biaodaa.controller;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.service.CompanyForUpdatedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 企业一键更新
 * Created by zhushuai on 2019/6/27.
 */
@Controller
@RequestMapping("/updated")
public class CompanyForUpdatedController extends BaseController{

    private static Logger logger = LoggerFactory.getLogger(CompanyForUpdatedController.class);

    @Autowired
    CompanyForUpdatedService companyForUpdatedService;

    /**
     * 待更新企业列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list/company", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> updatedCompanyList() {
        Map<String, Object> result = new HashMap<>();
        successMsg(result,companyForUpdatedService.listCompanyUpdated());
        return result;
    }

    /**
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/finish/company", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> updatedCompanyList(@RequestBody Map<String,Object> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            companyForUpdatedService.finishCompany(param);
            successMsg(result);
            return result;
        }catch (Exception e){
            logger.error("更新企业工商失败！",e);
            result.put("code", Constant.EXCEPTION_CODE);
            result.put("msg","更新企业工商失败！");
            return result;
        }
    }
}
