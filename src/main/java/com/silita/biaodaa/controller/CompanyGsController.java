package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.CompanyGsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhushuai on 2019/6/28.
 */
@Controller
@RequestMapping("/gs")
public class CompanyGsController extends BaseController {

    @Autowired
    CompanyGsService companyGsService;

    /**
     * 待更新企业列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/info", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> infoCompany(@RequestBody Map<String,Object> param) {
        Map<String, Object> result = new HashMap<>();
        successMsg(result,companyGsService.getGsCompangInfo(param));
        return result;
    }
}
