package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.google.common.base.Preconditions;
import com.silita.biaodaa.cache.GlobalCache;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.controller.vo.CompanyQual;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.service.CommonService;
import com.silita.biaodaa.service.NoticeService;
import com.silita.biaodaa.service.TbCompanyService;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.ObjectUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.silita.biaodaa.common.RedisConstantInterface.COM_OVER_TIME;

/**
 * Created by zhangxiahui on 18/4/4.
 */
@Controller
@RequestMapping("/company")
public class CompanyController extends BaseController {

    private static final Logger logger = Logger.getLogger(CompanyController.class);

    @Autowired
    private MyRedisTemplate myRedisTemplate;

    @Autowired
    TbCompanyService tbCompanyService;

    @Autowired
    CommonService commonService;

    @Autowired
    NoticeService noticeService;

    private GlobalCache globalCache = GlobalCache.getGlobalCache();

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

            Map<String,String> typeMap = new HashMap<>();
            typeMap.put("collType","company");
            noticeService.addCollStatus(pageInfo.getList(), typeMap);
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
            String province = MapUtils.getString(params, "province");
            Map<String,Object> param = new HashMap<>();
            String tableCode = "hunan";


            if(keyWord!=null&&!"".equals(keyWord)){
                // TODO: 18/5/21 更新排序移到打开人员处理
                //tbCompanyService.updatePersonPX(tableCode,keyWord);
            }
            param.put("keyWord",keyWord);
            if(comId!=null&&!"".equals(comId)){
                TbCompany company = tbCompanyService.getCompany(Integer.parseInt(comId));
                if(company!=null){
                    param.put("comId",comId);
                    param.put("comName",company.getComName());
                    province = company.getRegisAddress();
                    if(province.indexOf("湖南省")>-1){
                        province = "湖南省";
                    }
                }
            }
            if(province!=null&&!"".equals(province)){
                tableCode = tbCompanyService.getProvinceCode(province);
                if(tableCode==null){
                    result.put("code",0);
                    result.put("msg","该区域无法查询到数据");
                    return result;
                }
            }

            param.put("tableCode",tableCode);
            param.put("category",category);


            Integer pageNo = MapUtils.getInteger(params, "pageNo");
            Integer pageSize = MapUtils.getInteger(params, "pageSize");
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setCurrentPage(pageNo);
            PageInfo pageInfo  = tbCompanyService.getPersonCacheMap(page,param);
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
            result.put("data",getResultMap());
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
            String areaName = null;
            String [] areaNames = MyStringUtils.splitParam(regisAddress);
            if(areaNames!=null&&areaNames.length>0){
                areaName = areaNames[areaNames.length-1];
            }
            String indestry = MapUtils.getString(params, "indestry");
            Integer minCapital = MapUtils.getInteger(params, "minCapital");
            if(minCapital!=null&&minCapital==0){
                minCapital = null;
            }

            Integer maxCapital = MapUtils.getInteger(params, "maxCapital");
            String qualCode = MapUtils.getString(params, "qualCode");
            String keyWord = MapUtils.getString(params, "keyWord");

            String levelRank = MapUtils.getString(params, "levelRank");


            String code = "";
            String [] qualCodes = MyStringUtils.splitParam(qualCode);
            if(qualCodes!=null&&qualCodes.length>0){
                code = qualCodes[qualCodes.length-1];
            }
            Map<String,Object> param = new HashMap<>();
            param.put("areaName",areaName);
            param.put("indestry",indestry);
            param.put("minCapital",minCapital);
            param.put("maxCapital",maxCapital);
            param.put("qualCode",code);
            param.put("keyWord",keyWord);

            Integer pageNo = MapUtils.getInteger(params, "pageNo");
            Integer pageSize = MapUtils.getInteger(params, "pageSize");
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setCurrentPage(pageNo);

            PageInfo pageInfo  = tbCompanyService.filterCompany(page,param,levelRank);
            Map<String,String> typeMap = new HashMap<>();
            typeMap.put("collType","company");
            noticeService.addCollStatus(pageInfo.getList(), typeMap);
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

    @ResponseBody
    @RequestMapping(value = "/reputation/{comId}", method = RequestMethod.POST,produces = "application/json")
    public Map<String, Object> getCompanyReputation(@PathVariable Integer comId) {
        Map<String,Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "企业信誉查询失败!");

        try {
            Map<String,Object> map = tbCompanyService.getCompanyReputation(comId);
            result.put("data",map);
            result.put("code", 1);
            result.put("msg", "查询成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取企业信誉信息异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业信誉信息异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/undesirable/{comId}", method = RequestMethod.POST,produces = "application/json")
    public Map<String, Object> getUndesirable(@PathVariable Integer comId) {
        Map<String,Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "企业不良记录查询失败!");

        try {
            Map<String,Object> map = tbCompanyService.getUndesirable(comId);
            result.put("data",map);
            result.put("code", 1);
            result.put("msg", "查询企业不良记录成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取企业不良记录信息异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业不良记录信息异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    /**
     * 根据企业id获得logo
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getLogo", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> getLogo(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "获得企业logo成功！");
        try {
            Integer comId = (Integer) params.get("comId");
            Preconditions.checkArgument(null != comId && comId > 0, "comId不能为空且要大于0");
            String logo = tbCompanyService.getLogo(comId);
            result.put("data", logo);
            result.put("code", 1);
        } catch (Exception e) {
            logger.error("获得企业logo异常：" + e.getMessage());
            result.put("code", 0);
            result.put("msg", "获得企业logo失败！");
        }
        return result;
    }

    /**
     * 人员详细信息
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getPersonDetail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> getPersonDetail(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "获得企业人员详细信息成功！");
        try {
            Map<String, Object> data = tbCompanyService.getPersonDetail(params);
            result.put("data", data);
        } catch (Exception e) {
            logger.error("获得企业人员详细信息异常：" + e.getMessage());
            result.put("code", 0);
            result.put("msg", "获得企业人员详细信息失败！");
        }
        return result;
    }

    /**
     * 返回地区列表
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/province", method = RequestMethod.GET,produces = "application/json")
    public Map<String, Object> getProvince() {
        Map<String,Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "获取地区列表失败!");
        try {
            List<Map<String,Object>> list = tbCompanyService.getProvince();
            result.put("data",list);
            result.put("code", 1);
            result.put("msg", "获取地区列表成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取地区列表异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        } catch (Exception e) {
            logger.error("获取地区列表异常" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    /**
     * 获取公司详情根据名称
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/detail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String,Object> getCompanyDetail(@RequestBody Map<String, Object> params){
        Map<String,Object> result = new HashMap<>();
        result.put(CODE_FLAG,SUCCESS_CODE);
        result.put(MSG_FLAG,SUCCESS_MSG);
        TbCompany company = tbCompanyService.getCompanyDetailByName(params);
        if(null == company){
            result.put(CODE_FLAG,0);
            result.put(MSG_FLAG,"未查询到公司详情");
            return result;
        }
        result.put("data",company);
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/matchName", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String,Object> matchName(@RequestBody Map<String, Object> params){
        List<Map> nameList = null;
        Map<String,Object> result = new HashMap<>();
        String name = MapUtils.getString(params,"name");
        if(MyStringUtils.isNotNull(name)) {
            params.put("name","%"+name+"%");
            params.put("count",5);
            int paramHash = ObjectUtils.buildMapParamHash(params);
            String listKey = RedisConstantInterface.COM_NAME_MATCH + paramHash;
            nameList = (List<Map>)myRedisTemplate.getObject(listKey);
            if (nameList == null ) {
                nameList = tbCompanyService.matchName(params);
                if(nameList!=null) {
                    myRedisTemplate.setObject(listKey, nameList, COM_OVER_TIME);
                }
            }
        }
        result.put("data",nameList);
        successMsg(result);
        return result;
    }


    private Map<String,Object> getResultMap(){
        Map<String,Object> map = globalCache.getResultMap();
        if(MapUtils.isEmpty(map)){
            map = new HashMap<>();
            List<CompanyQual> companyQual = tbCompanyService.getCompanyQual();
            List<Map<String,String>> indestry =tbCompanyService.getIndustry();
            List<Map<String, Object>> pbMode = commonService.queryPbMode();
            List<Map<String,Object>> area = tbCompanyService.getArea();
            List<Map<String,Object>> proviceCityCounty = tbCompanyService.getProvinceCityCounty();
            map.put("companyQual",companyQual);
            map.put("indestry",indestry);
            map.put("pbMode",pbMode);
            map.put("area",area);
            map.put("proviceCityCounty",proviceCityCounty);
            if(null != map){
                globalCache.setResultMap(map);
            }
        }
        return map;
    }
}
