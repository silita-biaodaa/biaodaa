package com.silita.biaodaa.controller;

import com.silita.biaodaa.model.CarouselImage;
import com.silita.biaodaa.model.TbHotWords;
import com.silita.biaodaa.service.FoundationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

}
