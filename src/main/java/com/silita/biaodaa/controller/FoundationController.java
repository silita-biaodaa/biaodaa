package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.model.CarouselImage;
import com.silita.biaodaa.model.TbHotWords;
import com.silita.biaodaa.service.FoundationService;
import com.silita.biaodaa.service.TbCompanyService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础模块
 * Created by 91567 on 2018/4/12.
 */
@RequestMapping("/foundation")
@Controller
public class FoundationController extends BaseController {

    private static final Logger logger = Logger.getLogger(CompanyController.class);

    @Autowired
    private FoundationService foundationService;

    @Autowired
    TbCompanyService tbCompanyService;

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
     * @param params
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
            String userId = VisitInfoHolder.getUid();
            Preconditions.checkArgument(StringUtils.isNotBlank(userId), "未登录");
            logger.info(String.format("添加反馈意见，用户id:%s", userId));
            params.put("pid", userId);
            Preconditions.checkArgument(params.containsKey("problem") && !Strings.isNullOrEmpty((String)params.get("problem")), "problem不能为空！");
            foundationService.addFeedback(params);
        } catch (Exception e) {
            logger.error(String.format("反馈意见添加失败！%s", e.getMessage()));
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 申请保证金借款
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/borrow", method=RequestMethod.POST, produces="application/json;charset=utf-8")
    public Map<String, Object> borrow(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 1);
        result.put("msg", "申请保证金借款成功!");
        try {
            Preconditions.checkArgument(params.containsKey("region") && !Strings.isNullOrEmpty((String)params.get("region")), "region不能为空！");
            Preconditions.checkArgument(params.containsKey("projName") && !Strings.isNullOrEmpty((String)params.get("projName")), "projName不能为空！");
            Preconditions.checkArgument(params.containsKey("borrower") && !Strings.isNullOrEmpty((String)params.get("borrower")), "borrower不能为空！");
            Preconditions.checkArgument(params.containsKey("kbTime") && !Strings.isNullOrEmpty((String)params.get("kbTime")), "kbTime不能为空！");
            Preconditions.checkArgument(params.containsKey("phone"), "phone不能为空！");
            Preconditions.checkArgument(params.containsKey("money") && !Strings.isNullOrEmpty((String)params.get("money")), "money不能为空！");
            Object phoneObject = (Object) params.get("phone");
            if (phoneObject instanceof Number) {
                params.put("phone", String.valueOf(phoneObject));
            }
            foundationService.addBorrow(params);
        } catch (Exception e) {
            logger.error(String.format("申请保证金借款失败！%s", e.getMessage()));
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 行业链接筛选
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/links", method=RequestMethod.POST, produces="application/json;charset=utf-8")
    public Map<String, Object> links(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 1);
        result.put("msg", "行业链接筛选成功!");
        try {
            String region = (String) params.get("region");
            if (StringUtils.isNotBlank(region)) {
                params.remove("region");
                List<Map<String,Object>> areaList = tbCompanyService.getArea();
                String[] regions = StringUtils.split(region, "|");
                Preconditions.checkArgument(null != regions && regions.length > 0 && regions.length <= 2, "地区格式传入不正确！正确格式为：湖南省||长沙市");
                if (regions.length == 2) {
                    String key = regions[1];
                    if (key.contains("湘西")) {
                        key = "湘西土家族苗族自治州";
                    }
                    params.put("region", Lists.newArrayList(key));
                } else {
                    String key = regions[0];
                    boolean has = false;
                    for (Map<String, Object> areaMap : areaList) {
                        String name = (String) areaMap.get("name");
                        List<String> list = (List<String>) areaMap.get("list");
                        if (name.equals(key)) {
                            list.add(key);
                            params.put("region", list);
                            has = true;
                            break;
                        }
                    }
                    Preconditions.checkArgument(has != false, "地区格式传入不正确！正确格式为：湖南省||长沙市");
                }
            } else {
                params.put("region", new ArrayList<>());
            }
            if (!params.containsKey("region")) {
                params.put("region", new ArrayList<>());
            }
            Page page = buildPage(params);
            PageInfo pageInfo = foundationService.queryLinks(page, params);
            result.put("data", pageInfo.getList());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total", pageInfo.getTotal());
            result.put("pages", pageInfo.getPages());
        } catch (Exception e) {
            logger.error(String.format("行业链接筛选失败！%s", e.getMessage()));
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 客户常用链接
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value="/customUrls", method=RequestMethod.POST, produces="application/json;charset=utf-8")
    public Map<String, Object> customUrls(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 1);
        result.put("msg", "常用链接筛选成功!");
        try {
            /*String userId = VisitInfoHolder.getUid();
            if (StringUtils.isNotBlank(userId)) {
                logger.info(String.format("筛选常用链接，用户id:%s", userId));
                params.put("userId", userId);
            }*/
            String region = (String) params.get("region");
            if (StringUtils.isNotBlank(region)) {
                params.remove("region");
                List<Map<String,Object>> areaList = tbCompanyService.getArea();
                String[] regions = StringUtils.split(region, "|");
                Preconditions.checkArgument(null != regions && regions.length > 0 && regions.length <= 2, "地区格式传入不正确！正确格式为：湖南省||长沙市");
                if (regions.length == 2) {
                    String key = regions[1];
                    if (key.contains("湘西")) {
                        key = "湘西土家族苗族自治州";
                    }
                    params.put("region", Lists.newArrayList(key));
                } else {
                    String key = regions[0];
                    boolean has = false;
                    for (Map<String, Object> areaMap : areaList) {
                        String name = (String) areaMap.get("name");
                        List<String> list = (List<String>) areaMap.get("list");
                        if (name.equals(key)) {
                            list.add(key);
                            params.put("region", list);
                            has = true;
                            break;
                        }
                    }
                    Preconditions.checkArgument(has != false, "地区格式传入不正确！正确格式为：湖南省||长沙市");
                }
            } else {
                params.put("region", new ArrayList<>());
            }
            if (!params.containsKey("region")) {
                params.put("region", new ArrayList<>());
            }
            Page page = buildPage(params);
            PageInfo pageInfo = foundationService.queryCustomUrls(page, params);
            result.put("data", pageInfo.getList());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total", pageInfo.getTotal());
            result.put("pages", pageInfo.getPages());
        } catch (Exception e) {
            logger.error(String.format("常用链接筛选失败！%s", e.getMessage()));
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 获得省份和城市列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getProvinceCity", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> getProvinceCity() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 1);
        result.put("msg", "获取省份和城市列表成功!");
        try {
            List<Map<String, Object>> list = tbCompanyService.getArea();
            result.put("data", list);
        } catch (Exception e) {
            logger.error(String.format("获取省份和城市列表失败！%s", e.getMessage()));
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 获得招标项目列表
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getProjects", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> getProjects(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("code", 1);
        result.put("msg", "获取招标项目列表成功!");
        try {
            String city = (String) params.get("city");
            if (StringUtils.isNotBlank(city) && city.contains("市")) {
                params.put("city", city.replace("市", ""));
            }
            Page page = buildPage(params);
            PageInfo pageInfo = foundationService.queryProjects(page, params);
            result.put("data", pageInfo.getList());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total", pageInfo.getTotal());
            result.put("pages", pageInfo.getPages());
        } catch (Exception e) {
            logger.error(String.format("获取招标项目列表失败！%s", e.getMessage()));
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }
}
