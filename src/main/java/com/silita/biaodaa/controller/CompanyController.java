package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.controller.vo.CompanyQual;
import com.silita.biaodaa.es.ElasticseachService;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.service.*;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.ObjectUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.silita.biaodaa.common.RedisConstantInterface.LIST_OVER_TIME;

/**
 * 企业
 * Created by zhangxiahui on 18/4/4.
 */
@Controller
@RequestMapping("/company")
public class CompanyController extends BaseController {

    private static final Logger logger = Logger.getLogger(CompanyController.class);

    @Autowired
    MyRedisTemplate myRedisTemplate;
    @Autowired
    TbCompanyService tbCompanyService;
    @Autowired
    CommonService commonService;
    @Autowired
    NoticeService noticeService;
    @Autowired
    ElasticseachService elasticseachService;
    @Autowired
    TbCompanyInfoService tbCompanyInfoService;
    @Autowired
    CompanyGsService companyGsService;

    /**
     * 企业详情
     *
     * @param comId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/{comId}", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getCompany(@PathVariable String comId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "企业查询失败!");
        try {
            TbCompany tbCompany = tbCompanyService.getCompany(comId);
            //判断是否最新工商
            Map<String, Object> gsCom = this.setNewGsCompany(tbCompany);
            if (MapUtils.isNotEmpty(gsCom)) {
                return gsCom;
            }
            if (null != tbCompany && null != tbCompany.getEconomicType()) {
                tbCompany.setComType(tbCompany.getEconomicType());
            }
            if (null != tbCompany && null != tbCompany.getRegisCapital()) {
                tbCompany.setRegisCapital(tbCompany.getRegisCapital().replace("万元", "万人民币"));
            }
            tbCompany.setUpdated(null);
            result.put("data", tbCompany);
            result.put("code", 1);
            result.put("msg", "查询成功!");
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("获取企业信息异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业信息异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 企业资质
     *
     * @param comId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/qual/{comId}", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> queryCompanyQualification(@PathVariable String comId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "企业资质失败!");
        try {
            List<Map<String, Object>> queryQualList = tbCompanyService.queryQualList(comId);
            result.put("data", queryQualList);
            result.put("code", 1);
            result.put("msg", "查询成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取企业资质异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业资质异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 企业人员注册类别
     *
     * @param comId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/personCategory/{comId}", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getPersonCategory(@PathVariable String comId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "获取企业注册人员类别失败!");
        try {
            List<Map<String, Object>> list = tbCompanyService.getCompanyPersonCate(comId);
            result.put("data", list);
            result.put("code", 1);
            result.put("msg", "查询成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取企业注册人员类别异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业注册人员类别异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }


    /**
     * 企业人员列表
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/person", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> queryPerson(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "成功!");
        result.put("pageNum", 1);
        result.put("pageSize", 0);
        result.put("total", 0);
        result.put("pages", 1);


        try {
            checkArgument(MapUtils.isNotEmpty(params), "参数对象params不可为空!");
            String comId = MapUtils.getString(params, "comId");
            String category = MapUtils.getString(params, "category");
            String keyWord = MapUtils.getString(params, "keyWord");
            String province = MapUtils.getString(params, "province");
            String comName = MapUtils.getString(params, "comName");
            Map<String, Object> param = new HashMap<>();
            String tableCode = "hunan";


            if (keyWord != null && !"".equals(keyWord)) {
                //  18/5/21 更新排序移到打开人员处理
                //tbCompanyService.updatePersonPX(tableCode,keyWord);
                param.put("keyWord", keyWord);
            }
            if (comId != null && !"".equals(comId)) {
                TbCompany company = tbCompanyService.getCompany(comId);
                if (company != null) {
                    param.put("comId", comId);
                    param.put("comName", company.getComName());
                    province = company.getRegisAddress();
                    if (province.indexOf("湖南省") > -1) {
                        province = "湖南省";
                    }
                }
            }
            if (province != null && !"".equals(province)) {
                tableCode = tbCompanyService.getProvinceCode(province);
                if (tableCode == null) {
                    result.put("code", 0);
                    result.put("msg", "该区域无法查询到数据");
                    return result;
                }
            }

            param.put("tableCode", tableCode);
            if (MyStringUtils.isNotNull(category)) {
                param.put("category", category);
            }
            if (StringUtils.isNotEmpty(comName)) {
                param.put("comName", comName);
            }
            Integer pageNo = MapUtils.getInteger(params, "pageNo");
            Integer pageSize = MapUtils.getInteger(params, "pageSize");
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setCurrentPage(pageNo);
            param.put("pageNo", pageNo);
            param.put("pageSize", pageSize);
            PageInfo pageInfo = tbCompanyService.getPersonCacheMap(page, param);
            result.put("data", pageInfo.getList());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total", pageInfo.getTotal());
            result.put("pages", pageInfo.getPages());
        } catch (IllegalArgumentException e) {
            logger.error("获取企业注册人员列表异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            logger.error("获取人企业注册人员列表异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 筛选条件
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/filter", method = RequestMethod.GET, produces = "application/json")
    public Map<String, Object> queryCompanyQualification() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "筛选条件查询失败!");

        try {
            result.put("data", getResultMap());
            result.put("code", 1);
            result.put("msg", "筛选条件查询成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取企业资质异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业资质异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 企业筛选列表
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/query/filter", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> filterCompany(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "成功!");
        result.put("pageNum", 1);
        result.put("pageSize", 0);
        result.put("total", 0);
        result.put("pages", 1);


        try {
            checkArgument(MapUtils.isNotEmpty(params), "参数对象params不可为空!");
//            String regisAddress = MapUtils.getString(params, "regisAddress");
//            String areaName = null;
//            String [] areaNames = MyStringUtils.splitParam(regisAddress);
//            if(areaNames!=null&&areaNames.length>0){
//                areaName = areaNames[areaNames.length-1];
//            }
            String indestry = MapUtils.getString(params, "indestry");
            Integer minCapital = MapUtils.getInteger(params, "minCapital");
            if (minCapital != null && minCapital == 0) {
                minCapital = null;
            }

            Integer maxCapital = MapUtils.getInteger(params, "maxCapital");
            String qualCode = MapUtils.getString(params, "qualCode");
            String keyWord = MapUtils.getString(params, "keyWord");

            String levelRank = MapUtils.getString(params, "levelRank");
            String rangeType = MapUtils.getString(params, "rangeType");
            if (MyStringUtils.isNull(rangeType)) {
                rangeType = "or";
            }
            Integer isVip = MapUtils.getInteger(params, "isVip");
            if (isVip == null) {
                isVip = 0;
            }
            String isBei = MapUtils.getString(params, "isBei");
            String code = serQualCode(qualCode);
            Map<String, Object> param = new HashMap<>();
            param.put("regisAddress", params.get("regisAddress"));
            param.put("indestry", indestry);
            param.put("minCapital", minCapital);
            param.put("maxCapital", maxCapital);
            param.put("qualCode", StringUtils.strip(code, ","));
            param.put("keyWord", keyWord);
            param.put("rangeType", rangeType);
            param.put("isVip", isVip);
            param.put("isBei", isBei);
            param.put("honorCate", params.get("honorCate"));

            Integer pageNo = MapUtils.getInteger(params, "pageNo");
            Integer pageSize = MapUtils.getInteger(params, "pageSize");
            Page page = new Page();
            page.setPageSize(pageSize);
            page.setCurrentPage(pageNo);

            PageInfo pageInfo = tbCompanyService.filterCompany(page, param, levelRank);
            Map<String, String> typeMap = new HashMap<>();
            typeMap.put("collType", "company");
            noticeService.addCollStatus(pageInfo.getList(), typeMap);
            result.put("data", pageInfo.getList());
            result.put("pageNum", pageInfo.getPageNum());
            result.put("pageSize", pageInfo.getPageSize());
            result.put("total", pageInfo.getTotal());
            result.put("pages", pageInfo.getPages());
        } catch (IllegalArgumentException e) {
            logger.error("筛选企业列表异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            logger.error("筛选企业列表异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 企业信誉
     *
     * @param comId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/reputation/{comId}", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getCompanyReputation(@PathVariable String comId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "企业信誉查询失败!");
        try {
            Map<String, Object> map = tbCompanyService.getCompanyReputation(comId);
            result.put("data", map);
            result.put("code", 1);
            result.put("msg", "查询成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取企业信誉信息异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业信誉信息异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 企业不良行为
     *
     * @param comId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/undesirable/{comId}", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getUndesirable(@PathVariable String comId) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "企业不良记录查询失败!");
        try {
            Map<String, Object> map = tbCompanyService.getUndesirable(comId);
            result.put("data", map);
            result.put("code", 1);
            result.put("msg", "查询企业不良记录成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取企业不良记录信息异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            logger.error("获取企业不良记录信息异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 返回地区列表
     *
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/province", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getProvince() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "获取地区列表成功!");
        try {
            List<Map<String, Object>> list = myRedisTemplate.getList("filter_province");
            if (null != list && list.size() > 0) {
                result.put("data", list);
                return result;
            }
            list = tbCompanyService.getProvince();
            result.put("data", list);
            myRedisTemplate.setObject("filter_province", list);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("获取地区列表异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            logger.error("获取地区列表异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 返回资质列表
     *
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/filter/qual", method = RequestMethod.POST, produces = "application/json")
    public Map<String, Object> getQual(@RequestBody Map<String, Object> param) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = this.getQualMap(MapUtils.getString(param, "code"));
            result.put("code", 1);
            result.put("msg", "获取资质列表成功!");
        } catch (IllegalArgumentException e) {
            logger.error("获取资质列表异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        } catch (Exception e) {
            logger.error("获取资质列表异常" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }


    /**
     * 获取公司详情根据名称
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/detail", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> getCompanyDetail(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put(CODE_FLAG, SUCCESS_CODE);
        result.put(MSG_FLAG, SUCCESS_MSG);
        TbCompany company = tbCompanyService.getCompanyDetailByName(params);
        if (null == company) {
            result.put(CODE_FLAG, 0);
            result.put(MSG_FLAG, "未查询到公司详情");
            return result;
        }
        Map<String, Object> gsCom = this.setNewGsCompany(company);
        if (MapUtils.isNotEmpty(gsCom)) {
            return gsCom;
        }
        result.put("data", company);
        return result;
    }

    /**
     * 根据企业名称检索
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/matchName", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> matchName(@RequestBody Map<String, Object> params) {
        List<Map<String, Object>> nameList = null;
        Map<String, Object> result = new HashMap<>();
        String name = MapUtils.getString(params, "name");
        if (MyStringUtils.isNotNull(name)) {
            params.put("name", "*" + name.toLowerCase() + "*");
            params.put("count", 5);
            nameList = elasticseachService.quary(params);
        }
        result.put("data", nameList);
        successMsg(result);
        return result;
    }


    /**
     * 封装筛选条件
     *
     * @return
     */
    private Map<String, Object> getResultMap() {
        Map<String, Object> map = (Map<String, Object>) myRedisTemplate.getObject("filter_map");
        if (MapUtils.isEmpty(map)) {
            map = new HashMap<>();
            List<CompanyQual> companyQual = tbCompanyService.getCompanyQual();
            List<Map<String, String>> indestry = tbCompanyService.getIndustry();
            List<Map<String, Object>> pbMode = commonService.queryPbMode();
            List<Map<String, Object>> area = tbCompanyService.getArea();
            List<Map<String, Object>> proviceCityCounty = tbCompanyService.getProvinceCityCounty();
            map.put("companyQual", companyQual);
            map.put("indestry", indestry);
            map.put("pbMode", pbMode);
            map.put("area", area);
            map.put("proviceCityCounty", proviceCityCounty);
            if (null != map) {
                myRedisTemplate.setObject("filter_map", map);
            }
        }
        return map;
    }

    /**
     * 封装资质
     *
     * @return
     */
    private Map<String, Object> getQualMap(String code) {
        String key = myRedisTemplate.buildKey(code);
        Map<String, Object> map = (Map<String, Object>) myRedisTemplate.getObject(key);
        if (MapUtils.isEmpty(map)) {
            map = new HashMap<>();
            List<CompanyQual> companyQual = tbCompanyService.getCompanyQual();
            Iterator<CompanyQual> qualIterator = companyQual.iterator();
            while (qualIterator.hasNext()) {
                CompanyQual comQual = qualIterator.next();
                if ("城市园林绿化".equals(comQual.getName())) {
                    qualIterator.remove();
                }
                if ("hunan".equals(code) && "其他".equals(comQual.getName())) {
                    Iterator<CompanyQual> iterator = comQual.getList().iterator();
                    while (iterator.hasNext()) {
                        CompanyQual childQual = iterator.next();
                        if (!"文物保护工程施工".equals(childQual.getName())) {
                            iterator.remove();
                        }
                    }
                } else {
                    if ("其他".equals(comQual.getName()) || "公路养护".equals(comQual.getName())) {
                        qualIterator.remove();
                    }
                }
            }
            map.put("data", companyQual);
            if (null != map) {
                myRedisTemplate.setObject(key, map);
            }
        }
        return map;
    }


    /**
     * 热门企业
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/host", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> hostCompanyList(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        Integer isVip = MapUtils.getInteger(param, "isVip");
        if (isVip == null) {
            isVip = 0;
            param.put("isVip", isVip);
        }
        List<TbCompany> companyList = tbCompanyService.getHostCompanyList(param);
        Map<String, String> typeMap = new HashMap<>();
        typeMap.put("collType", "company");
        noticeService.addCollStatus(companyList, typeMap);
        resultMap.put("data", companyList);
        return resultMap;
    }

    /**
     * 分支机构
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/branchCompany", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> branchCompany(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        String comId = MapUtils.getString(param, "comId");
        param.put("comId", comId);
        Object object = elasticseachService.queryBranchCompany(param);
        if (object instanceof Map) {
            resultMap = (Map<String, Object>) object;
            successMsg(resultMap);
            return resultMap;
        }
        resultMap.put("data", object);
        return resultMap;
    }

    /**
     * 企业分享-业绩/人员/分支机构个数
     *
     * @param param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/shareTotal", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> shareTotal(@RequestBody Map<String, Object> param) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        Map<String, Object> personMap = this.queryPerson(param);
        Map<String, Object> valueMap = tbCompanyService.getShareTotal(param);
        valueMap.put("personTotal", personMap.get("total"));
        resultMap.put("data", valueMap);
        return resultMap;
    }

    /**
     * 分支机构es导入
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/esBranchCompany", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> esBranchCompany() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", this.SUCCESS_CODE);
        resultMap.put("msg", this.SUCCESS_MSG);
        elasticseachService.batchAddBranchCompany();
        return resultMap;
    }

    /**
     * 中标列表
     *
     * @param params
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/notice/list", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    public Map<String, Object> companyNoticeList(@RequestBody Map<String, Object> params) {
        int paramHash = ObjectUtils.buildMapParamHash(params);
        String listKey = RedisConstantInterface.GG_LIST_COMPANY + paramHash;
        logger.info("公告列表key:" + listKey + "--------------------");
        List<Map> list = (List<Map>) myRedisTemplate.getObject(listKey);
        if (null == list) {
            list = noticeService.getCompanyZhongbiaoList(params);
            if (null != list && list.size() > 0) {
                myRedisTemplate.setObject(listKey, list, LIST_OVER_TIME);
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", list);
        successMsg(resultMap);
        return resultMap;
    }

    private Map<String, Object> setNewGsCompany(TbCompany company) {
        Map<String, Object> result = new HashedMap();
        //判断是否最新工商
        Map gsCom = companyGsService.getGsCompany(company);
        if (MapUtils.isNotEmpty(gsCom)) {
            result.put("data", gsCom);
            result.put("code", 1);
            result.put("msg", "查询成功!");
            return result;
        }
        return null;
    }
}
