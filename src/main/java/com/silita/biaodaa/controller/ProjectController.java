package com.silita.biaodaa.controller;

import com.silita.biaodaa.service.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 业绩
 */
@Controller
@RequestMapping("/project")
public class ProjectController extends BaseController {

    @Autowired
    ProjectService projectService;
    @Autowired
    ProjectZhaotoubiaoService projectZhaotoubiaoService;
    @Autowired
    ProjectDesignService projectDesignService;
    @Autowired
    ProjectBuildService projectBuildService;
    @Autowired
    ProjectContractService projectContractService;
    @Autowired
    ProjectCompletionService projectCompletionService;

    /**
     * TODO: 业绩筛选
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> query(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", this.SUCCESS_CODE);
        result.put("msg", this.SUCCESS_MSG);
        if ("project".equals(params.get("tabType").toString())) {
            projectService.getProjectList(params, result);
            return result;
        }
        projectService.getObjectListByUnit(params, result);
        return result;
    }

    /**
     * TODO: 获取项目基本详情
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/detail", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> detail(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", this.SUCCESS_CODE);
        result.put("msg", this.SUCCESS_MSG);
        projectService.getProjectDetail(MapUtils.getString(param, "id"), result);
        return result;
    }

    /**
     * TODO: 获取tab列表
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/company/detail", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> companyDetail(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", this.SUCCESS_CODE);
        result.put("msg", this.SUCCESS_MSG);
        projectService.getTabProjectDetail(param, result);
        return result;
    }

    /**
     * TODO: 获取tab列表下的详情
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list/detail", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> tabListDatail(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", this.SUCCESS_CODE);
        result.put("msg", this.SUCCESS_MSG);
        if ("zhaotoubiao".equals(param.get("tabType").toString())) {
            result.put("data", projectZhaotoubiaoService.getProjectZhaotoubiaoDetail(param));
        } else if ("design".equals(param.get("tabType").toString())) {
            result.put("data", projectDesignService.getProjectDesignDetail(param));
        } else if ("build".equals(param.get("tabType").toString())) {
            result.put("data", projectBuildService.getProjectDetail(param));
        } else if ("contract".equals(param.get("tabType").toString())) {
            result.put("data", projectContractService.getProjectContractDetail(param));
        } else if ("completion".equals(param.get("tabType").toString())) {
            result.put("data", projectCompletionService.getProCompletDetail(param));
        }
        return result;
    }

    /**
     * TODO: 企业下的业绩列表
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/company/list", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> companyProjectList(@RequestBody Map<String, Object> param) {
        Map<String, Object> result  = projectService.getProjectCompanyList(param);
        successMsg(result);
        return result;
    }

    /**
     * TODO: 修改
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/update", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> analysisData() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", this.SUCCESS_CODE);
        result.put("msg", this.SUCCESS_MSG);
        projectService.analysisData();
        return result;
    }
}