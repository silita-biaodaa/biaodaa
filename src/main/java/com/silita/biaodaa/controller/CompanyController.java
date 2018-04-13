package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.CompanyQual;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.service.CommonService;
import com.silita.biaodaa.service.TbCompanyService;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by zhangxiahui on 18/4/4.
 */
@Controller
@RequestMapping("/company")
public class CompanyController {

    private static final Logger logger = Logger.getLogger(CompanyController.class);

    @Autowired
    TbCompanyService tbCompanyService;

    @Autowired
    CommonService commonService;


    @ResponseBody
    @RequestMapping(value = "/query", method = RequestMethod.POST,produces = "application/json")
    public Map<String, Object> queryCompany(@RequestBody Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "成功!");
        result.put("pageNum",1);
        result.put("pageSize", 0);
        result.put("total",0);
        result.put("pages",1);


        try {
            checkArgument(MapUtils.isNotEmpty(params), "参数对象params不可为空!");
            String keyWord = MapUtils.getString(params, "keyWord");
            Integer pageNo = MapUtils.getInteger(params, "pageNo");
            Integer pageSize = MapUtils.getInteger(params, "pageSize");
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setCurrentPage(pageNo);
            if(keyWord==null){
                keyWord = "";
            }
            PageInfo pageInfo  = tbCompanyService.queryCompanyList(page,keyWord);
            result.put("data",pageInfo.getList());
            result.put("pageNum",pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total",pageInfo.getTotal());
            result.put("pages",pageInfo.getPages());
        } catch (IllegalArgumentException e) {
            logger.error("获取企业列表异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        } catch (Exception e) {
            logger.error("获取人企业列表异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }


    @ResponseBody
    @RequestMapping(value = "/{comId}", method = RequestMethod.POST,produces = "application/json")
    public Map<String, Object> getCompany(@PathVariable Integer comId) {
        Map<String,Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "企业查询失败!");

        try {
            TbCompany tbCompany = tbCompanyService.getCompany(comId);
            result.put("data",tbCompany);
            result.put("code", 1);
            result.put("msg", "查询成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取企业信息异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业信息异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/qual/{comId}", method = RequestMethod.POST,produces = "application/json")
    public Map<String, Object> queryCompanyQualification(@PathVariable Integer comId) {
        Map<String,Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "企业资质失败!");

        try {
            List<Map<String,Object>> queryQualList = tbCompanyService.queryQualList(comId);
            result.put("data",queryQualList);
            result.put("code", 1);
            result.put("msg", "查询成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取企业资质异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业资质异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/personCategory/{comId}", method = RequestMethod.POST,produces = "application/json")
    public Map<String, Object> getPersonCategory(@PathVariable Integer comId) {
        Map<String,Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "获取企业注册人员类别失败!");

        try {
            List<Map<String,Object>> list = tbCompanyService.getCompanyPersonCate(comId);
            result.put("data",list);
            result.put("code", 1);
            result.put("msg", "查询成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取企业注册人员类别异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业注册人员类别异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/person", method = RequestMethod.POST,produces = "application/json")
    public Map<String, Object> queryPerson(@RequestBody Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "成功!");
        result.put("pageNum",1);
        result.put("pageSize", 0);
        result.put("total",0);
        result.put("pages",1);


        try {
            checkArgument(MapUtils.isNotEmpty(params), "参数对象params不可为空!");
            String comId = MapUtils.getString(params, "comId");
            String category = MapUtils.getString(params, "category");
            String keyWord = MapUtils.getString(params, "keyWord");
            Map<String,Object> param = new HashMap<>();
            param.put("comId",comId);
            param.put("category",category);
            param.put("keyWord",keyWord);



            Integer pageNo = MapUtils.getInteger(params, "pageNo");
            Integer pageSize = MapUtils.getInteger(params, "pageSize");
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setCurrentPage(pageNo);

            PageInfo pageInfo  = tbCompanyService.queryCompanyPerson(page,param);
            result.put("data",pageInfo.getList());
            result.put("pageNum",pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total",pageInfo.getTotal());
            result.put("pages",pageInfo.getPages());
        } catch (IllegalArgumentException e) {
            logger.error("获取企业注册人员列表异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        } catch (Exception e) {
            logger.error("获取人企业注册人员列表异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/filter", method = RequestMethod.GET,produces = "application/json")
    public Map<String, Object> queryCompanyQualification() {
        Map<String,Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "筛选条件查询失败!");

        try {
            List<CompanyQual> companyQual = tbCompanyService.getCompanyQual();
            List<Map<String,String>> indestry =tbCompanyService.getIndustry();
            List<Map<String, Object>> pbMode = commonService.queryPbMode();
            List<Map<String,Object>> area = tbCompanyService.getArea();
            Map<String,Object> map = new HashMap<>();
            map.put("companyQual",companyQual);
            map.put("indestry",indestry);
            map.put("pbMode",pbMode);
            map.put("area",area);
            result.put("data",map);
            result.put("code", 1);
            result.put("msg", "筛选条件查询成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取企业资质异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业资质异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/query/filter", method = RequestMethod.POST,produces = "application/json")
    public Map<String, Object> filterCompany(@RequestBody Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "成功!");
        result.put("pageNum",1);
        result.put("pageSize", 0);
        result.put("total",0);
        result.put("pages",1);


        try {
            checkArgument(MapUtils.isNotEmpty(params), "参数对象params不可为空!");
            String regisAddress = MapUtils.getString(params, "regisAddress");
            String areaName = "";
            String [] areaNames = MyStringUtils.splitParam(regisAddress);
            if(areaNames!=null&&areaNames.length>0){
                areaName = areaNames[areaNames.length-1];
            }
            String areaCode = tbCompanyService.getAreaCode(areaName);
            if(regisAddress!=null&&!"".equals(regisAddress)&&areaCode==null){
                areaCode = "99999";//查询结果为空
            }
            String indestry = MapUtils.getString(params, "indestry");
            Integer minCapital = MapUtils.getInteger(params, "minCapital");
            Integer maxCapital = MapUtils.getInteger(params, "maxCapital");
            String qualCode = MapUtils.getString(params, "qualCode");
            String code = "";
            String [] qualCodes = MyStringUtils.splitParam(qualCode);
            if(qualCodes!=null&&qualCodes.length>0){
                code = qualCodes[qualCodes.length-1];
            }
            Map<String,Object> param = new HashMap<>();
            param.put("areaCode",areaCode);
            param.put("indestry",indestry);
            param.put("minCapital",minCapital);
            param.put("maxCapital",maxCapital);
            param.put("qualCode",code);

            Integer pageNo = MapUtils.getInteger(params, "pageNo");
            Integer pageSize = MapUtils.getInteger(params, "pageSize");
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setCurrentPage(pageNo);

            PageInfo pageInfo  = tbCompanyService.filterCompany(page,param);
            result.put("data",pageInfo.getList());
            result.put("pageNum",pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total",pageInfo.getTotal());
            result.put("pages",pageInfo.getPages());
        } catch (IllegalArgumentException e) {
            logger.error("筛选企业列表异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        } catch (Exception e) {
            logger.error("筛选企业列表异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }


}
