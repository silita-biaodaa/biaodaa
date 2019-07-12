package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.es.ElasticseachService;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyInfo;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 工商企业
 * Created by zhushuai on 2019/6/28.
 */
@Service
public class CompanyGsService {

    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    CompanyHbaseService companyHbaseService;
    @Autowired
    ElasticseachService elasticseachService;
    @Autowired
    TbCompanyService tbCompanyService;

    /**
     * 工商基本信息
     *
     * @param company
     * @return
     */
    public Map<String, Object> getGsCompany(TbCompany company) {
        Map<String, Object> param = new HashedMap();
        if (null != company && null != company.getComName()) {
            param.put("comName", company.getComName());
        }
        String comId = companyHbaseService.getCompanyReportComId(param);
        if(StringUtils.isEmpty(comId)){
            return null;
        }
        param.put("comId",comId);
        Map<String, Object> comMap = companyHbaseService.getGsCompany(param);
        if (MapUtils.isEmpty(comMap)) {
            return null;
        }
        Map<String, Object> resultMap = (Map<String, Object>) comMap.get("basic");
        resultMap.put("updated", comMap.get("updated"));
        resultMap.put("comId", company.getComId());
        resultMap.put("phone", company.getPhone());
        resultMap.put("email", company.getEmail());
        resultMap.put("comUrl", company.getComUrl());
        resultMap.put("collected", company.getCollected());
        resultMap.put("isUpdated", 1);
        resultMap.put("certNo", company.getCertNo());
        resultMap.put("validDate", company.getValidDate());
        if (null == resultMap.get("comAddress")) {
            resultMap.put("comAddress", company.getComAddress());
        }
        if (null == resultMap.get("comType") && null != company && null != company.getEconomicType()) {
            resultMap.put("comType", company.getEconomicType());
        }
        comMap = null;
        company = null;
        return resultMap;
    }

    /**
     * 查询股东信息、主要人员、变更记录、行政处罚
     *
     * @param param
     * @return
     */
    public Object getGsCompangInfo(Map<String, Object> param) {
        TbCompany company = tbCompanyService.getCompany(MapUtils.getString(param, "comId"));
        if (null != company && null != company.getComName()) {
            param.put("comName", company.getComName());
        }
        String comId = companyHbaseService.getCompanyReportComId(param);
        if(StringUtils.isEmpty(comId)){
            return null;
        }
        param.put("comId",comId);
        Map<String, Object> comMap = companyHbaseService.getGsCompany(param);
        Object resultObj = null;
        String paramter = MapUtils.getString(param, "paramter");
        if (MapUtils.isNotEmpty(comMap) && null != comMap.get(paramter)) {
            resultObj = (List) JSONObject.parse(MapUtils.getString(comMap, paramter));
            return resultObj;
        }
        return null;
    }

    /**
     * 查询股东信息、主要人员、变更记录、企业年报、行政处罚个数
     *
     * @param param
     * @return
     */
    public Map<String, Object> getGsCompanyCount(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap() {{
            put("partner", 0);
            put("personnel", 0);
            put("changeRecord", 0);
            put("punish", 0);
            put("branchCompany", 0);
            put("report", 0);
        }};
        TbCompany company = tbCompanyService.getCompany(MapUtils.getString(param, "comId"));
        if (null != company && null != company.getComName()){
            param.put("comName",company.getComName());
        }
        String comId = companyHbaseService.getCompanyReportComId(param);
        if (StringUtils.isEmpty(comId)){
            return resultMap;
        }
        param.put("comId",comId);
        Map<String, Object> obj = companyHbaseService.getGsCompany(param);
        List list;
        //股东信息
        if (MapUtils.isNotEmpty(obj) && null != obj.get("partner")) {
            list = (List) JSONObject.parse(MapUtils.getString(obj, "partner"));
            resultMap.put("partner", list.size());
        }
        //主要人员
        if (MapUtils.isNotEmpty(obj) && null != obj.get("personnel")) {
            list = (List) JSONObject.parse(MapUtils.getString(obj, "personnel"));
            resultMap.put("personnel", list.size());
        }
        //变更记录
        if (MapUtils.isNotEmpty(obj) && null != obj.get("changeRecord")) {
            list = (List) JSONObject.parse(MapUtils.getString(obj, "changeRecord"));
            resultMap.put("changeRecord", list.size());
        }
        //处罚信息
        if (MapUtils.isNotEmpty(obj) && null != obj.get("punish")) {
            list = (List) JSONObject.parse(MapUtils.getString(obj, "punish"));
            resultMap.put("punish", list.size());
        }
        //分支机构
        List<TbCompanyInfo> companyInfoList = (List<TbCompanyInfo>) elasticseachService.queryBranchCompany(param);
        if (null != companyInfoList && companyInfoList.size() > 0) {
            resultMap.put("branchCompany", companyInfoList.size());
            companyInfoList = null;
        }
        //年报个数
        List<Map<String, Object>> reportList = companyHbaseService.getCompanyReportYear(param);
        if (null != reportList && reportList.size() > 0) {
            resultMap.put("report", reportList.size());
            reportList = null;
        }
        param = null;
        obj = null;
        return resultMap;
    }

    /**
     * 获取年份
     *
     * @param param
     * @return
     */
    public List<Map<String, Object>> getReportYears(Map<String, Object> param) {
        TbCompany company = tbCompanyService.getCompany(MapUtils.getString(param, "comId"));
        if (null != company) {
            param.put("comName", company.getComName());
        }
        return companyHbaseService.getCompanyReportYear(param);
    }

    /**
     * 获取年报详情
     *
     * @param param
     * @return
     */
    public Map<String, Object> getReportDetail(Map<String, Object> param) {
        TbCompany company = tbCompanyService.getCompany(MapUtils.getString(param, "comId"));
        if (null != company) {
            param.put("comName", company.getComName());
        }
        Map<String, Object> report = companyHbaseService.getCompanyReport(param);
        if (MapUtils.isNotEmpty(report)) {
            return setAttrMap(report);
        }
        return new HashedMap();
    }

    private Map<String, Object> setAttrMap(Map<String, Object> param) {
        Map<String, Object> basic = null;
        Object objectMap = param.get("basic");
        if (objectMap instanceof JSONArray) {
            basic = (Map<String, Object>) ((List) param.get("basic")).get(0);
        } else if (objectMap instanceof Map) {
            basic = (Map<String, Object>) param.get("basic");
        }
        param.put("basic", this.setMap(basic));
        if (null != param.get("partner")) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) param.get("partner");
            List partner = new ArrayList();
            for (Map<String, Object> map : list) {
                partner.add(setMap(map));
            }
            param.put("partner", partner);
        }
        if (null != param.get("socialSecurity")) {
            List<Map<String, Object>> list = (List<Map<String, Object>>) param.get("socialSecurity");
            param.put("socialSecurity", setSocialSecurityMap(list));
        }
        return param;
    }

    private Map setMap(Map<String, Object> param) {
        List<String> keyList = new ArrayList<>();
        List<String> constanCnList = new ArrayList<>();
        Set<String> keys = param.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            if (key.endsWith("Dis")) {
                keyList.add(key);
            }
            if (key.endsWith("cn") || key.endsWith("CN")) {
                constanCnList.add(key);
            }
        }
        String value;
        //合并带cn的属性
        if (null != constanCnList && constanCnList.size() > 0) {
            for (String consCnKey : constanCnList) {
                value = MapUtils.getString(param, consCnKey);
                param.put(consCnKey.replace("_cn", "").replace("_CN", ""), value);
                param.remove(consCnKey);
            }
        }
        if (null != keyList && keyList.size() > 0) {
            for (String disKey : keyList) {
                if ("2".equals(param.get(disKey))) {
                    param.put(disKey.replace("Dis", ""), "企业选择不公示");
                }
            }
        }
        return param;
    }

    /**
     * 设置社保显示
     *
     * @param socialSecurity
     * @return
     */
    private List<Map<String, Object>> setSocialSecurityMap(List<Map<String, Object>> socialSecurity) {
        for (Map<String, Object> map : socialSecurity) {
            List<String> keys = new ArrayList<>();
            Set<String> setKets = map.keySet();
            Iterator<String> iterator = setKets.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (key.endsWith("Dis")) {
                    keys.add(key);
                }
            }
            if (null != keys && keys.size() > 0) {
                List<String> disKey = new ArrayList<>();
                for (String key : keys) {
                    iterator = setKets.iterator();
                    while (iterator.hasNext()) {
                        String mapKey = iterator.next();
                        if ("0".equals(map.get(key).toString()) && mapKey.contains(key.replace("Dis", "")) && !mapKey.equals(key)) {
                            disKey.add(mapKey);
                        }
                    }
                }
                if (null != disKey && disKey.size() > 0) {
                    for (String key : disKey) {
                        map.put(key, "企业选择不公示");
                    }
                }
            }

        }
        return socialSecurity;
    }
}
