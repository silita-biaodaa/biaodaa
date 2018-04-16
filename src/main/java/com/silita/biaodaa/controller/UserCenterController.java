package com.silita.biaodaa.controller;

/**
 * Created by 91567 on 2018/4/12.
 */

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.model.UserTempBdd;
import com.silita.biaodaa.service.UserCenterService;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 用户信息模块
 * Created by 91567 on 2018/4/12.
 */
@RequestMapping("/userCenter")
@Controller
public class UserCenterController {
    private static final Logger logger = Logger.getLogger(CompanyController.class);

    @Autowired
    private UserCenterService userCenterService;


    @ResponseBody
    @RequestMapping(value = "/updateUserTemp",produces = "application/json;charset=utf-8")
    public Map<String,Object> updateUserTemp(@RequestBody UserTempBdd userTempBdd){
        Map result = new HashMap();
        result.put("code", 1);
        result.put("data", null);

        try{
            UserTempBdd vo = userCenterService.updateUserTemp(userTempBdd);
            if(vo != null) {
                result.put("msg", "更新账号基本信息成功！");
                result.put("data", vo);
            }
        } catch (Exception e) {
            logger.error("更新账号基本信息异常！" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/listMessageByUserId", produces = "application/json;charset=utf-8")
    public Map<String, Object> listMessageByUserId(@RequestBody Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "获取消息列表成功!");
        result.put("pageNum",1);
        result.put("pageSize", 0);
        result.put("total",0);
        result.put("pages",1);

        try{
            checkArgument(MapUtils.isNotEmpty(params), "参数对象params不可为空!");
            Page page = new Page();
            page.setPageSize(MapUtils.getInteger(params, "pageSize"));
            page.setCurrentPage(MapUtils.getInteger(params, "pageNo"));

            PageInfo pageInfo  = userCenterService.queryMessageList(page, params);
            result.put("data",pageInfo.getList());
            result.put("pageNum",pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total",pageInfo.getTotal());
            result.put("pages",pageInfo.getPages());
        } catch (Exception e) {
            logger.error("获消息列表异常！" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }
}
