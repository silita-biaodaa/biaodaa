package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.service.TbCompanyService;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    @RequestMapping(value = "/{comId}", method = RequestMethod.GET,produces = "application/json")
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
}
