package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.PhoneAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhushuai on 2019/3/7.
 */
@Controller
@RequestMapping("/api")
public class ApiController extends BaseController {

    @Autowired
    PhoneAddressService phoneAddressService;

    @ResponseBody
    @RequestMapping(value = "/list", produces = "application/json;charset=utf-8")
    public Map<String, Object> list(@RequestBody Map<String, Object> param) {
        Map result = new HashMap();
        successMsg(result);
        successMsg(result, phoneAddressService.listPhone(param));
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/export", produces = "application/json;charset=utf-8")
    public void exprotPhone(HttpServletResponse response, @RequestBody Map<String, Object> param) {
        phoneAddressService.export(response,param);
    }
}