package com.silita.biaodaa.controller;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.silita.biaodaa.model.CarouselImage;
import com.silita.biaodaa.model.TbHotWords;
import com.silita.biaodaa.service.FoundationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础模块
 * Created by 91567 on 2018/4/12.
 */
@RequestMapping("/foundation")
@Controller
public class FoundationController {

    private static final Logger logger = Logger.getLogger(CompanyController.class);

    @Autowired
    private FoundationService foundationService;

    @ResponseBody
    @RequestMapping(value = "/listBannerImage",produces = "application/json;charset=utf-8")
    public Map<String,Object> listBannerImage(@RequestBody Map params){
        Map result = new HashMap();
        result.put("code", 1);
        result.put("msg", "获取轮播图信息成功!");

        try{
            List<CarouselImage> carouselImages = foundationService.queryCarouselImageList(params);
            result.put("data", carouselImages);
        } catch (Exception e) {
            logger.error("获取轮播图信息异常！" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/listHotHotWords",produces = "application/json;charset=utf-8")
    public Map<String,Object> listHotHotWords(@RequestBody Map params){
        Map result = new HashMap();
        result.put("code", 1);
        result.put("msg", "获取热词信息成功!");

        try{
            List<TbHotWords> hotWordses = foundationService.queryHotWordsByTypeList(params);
            result.put("data", hotWordses);
        } catch (Exception e) {
            logger.error("获取热词信息异常！" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    /**
     * 版本查询
     * @param loginChannel
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/getVersion", produces="application/json;charset=utf-8")
    public Map<String, Object> getVersion(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 1);
        result.put("msg", "获取版本信息成功!");
        try {
            Preconditions.checkArgument(params.containsKey("loginChannel") && !Strings.isNullOrEmpty((String) params.get("loginChannel")), "loginChannel不能为空！");
            String loginChannel = (String) params.get("loginChannel");
            String version = foundationService.getVersion(loginChannel);
            Preconditions.checkArgument(!Strings.isNullOrEmpty(version), "version信息不能为空！");
            result.put("data", version);
        } catch (Exception e) {
            logger.error(String.format("版本查询失败！%s", e.getMessage()));
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 添加反馈意见
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/addFeedback", method = RequestMethod.POST, produces="application/json;charset=utf-8")
    public Map<String, Object> addFeedback(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 1);
        result.put("msg", "反馈意见添加成功!");
        try {
            Preconditions.checkArgument(params.containsKey("pid") && !Strings.isNullOrEmpty((String)params.get("pid")), "pid不能为空！");
            Preconditions.checkArgument(params.containsKey("problem") && !Strings.isNullOrEmpty((String)params.get("problem")), "problem不能为空！");
            foundationService.addFeedback(params);
        } catch (Exception e) {
            logger.error(String.format("反馈意见添加失败！%s", e.getMessage()));
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }
}
