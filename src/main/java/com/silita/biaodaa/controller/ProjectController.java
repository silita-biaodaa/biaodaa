package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.ProjectService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/project")
public class ProjectController extends BaseController{

    @Autowired
    ProjectService projectService;

    /**
     * 业绩筛选
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.POST,produces = "application/json")
    public Map<String,Object> query(@RequestBody Map<String, Object> params){
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("code",this.SUCCESS_CODE);
        result.put("msg",this.SUCCESS_MSG);
        if("project".equals(params.get("tabType").toString())){
            projectService.getProjectList(params,result);
            return  result;
        }
        projectService.getObjectListByUnit(params,result);
        return  result;
    }

    /**
     * 获取项目基本详情
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/detail",method = RequestMethod.POST,produces = "application/json")
    public Map<String,Object> detail(@RequestBody Map<String, Object> param){
        Map<String,Object> result = new HashMap<String, Object>();
        result.put("code",this.SUCCESS_CODE);
        result.put("msg",this.SUCCESS_MSG);
        projectService.getProjectDetail(MapUtils.getInteger(param,"id"),result);
        return result;
    }

    /**
     * 获取tab列表
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/company/detail",method = RequestMethod.POST,produces = "application/json")
    public Map<String,Object> companyDetail(@RequestBody Map<String, Object> param){
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("code",this.SUCCESS_CODE);
        result.put("msg",this.SUCCESS_MSG);
        projectService.getTabProjectDetail(param,result);
        return result;
    }
}
