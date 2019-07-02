package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.dao.TbCompanyReportMapper;
import com.silita.biaodaa.dao.TbGsCompanyMapper;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 工商企业
 * Created by zhushuai on 2019/6/28.
 */
@Service
public class CompanyGsService {

    @Autowired
    TbGsCompanyMapper tbGsCompanyMapper;
    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    TbCompanyReportMapper tbCompanyReportMapper;

    /**
     * 工商基本信息
     *
     * @param company
     * @return
     */
    public Map<String, Object> getGsCompany(TbCompany company) {
        if (tbGsCompanyMapper.queryCompanyExits(company.getComId()) <= 0) {
            return null;
        }
        Map<String, Object> param = new HashedMap() {{
            put("paramter", "basic");
        }};
        param.put("comId", company.getComId());
        Map<String, Object> comMap = tbGsCompanyMapper.queryCompanyParamter(param);
        Map<String, Object> resultMap = JSONObject.parseObject(comMap.get("paramter").toString());
        resultMap.put("updated", comMap.get("updated"));
        resultMap.put("comId", company.getComId());
        resultMap.put("phone", company.getPhone());
        resultMap.put("email", company.getEmail());
        resultMap.put("comUrl", company.getComUrl());
        resultMap.put("collected", company.getCollected());
        resultMap.put("isUpdated", 1);
        resultMap.put("certNo", company.getCertNo());
        resultMap.put("validDate", company.getValidDate());
        if (null == resultMap.get("regisAddress")){
            resultMap.put("regisAddress", company.getRegisAddress());
        }
        comMap = null;
        company = null;
        return resultMap;
    }

    /**
     * 查询股东信息、主要人员、变更记录、企业年报、行政处罚
     *
     * @param param
     * @return
     */
    public Object getGsCompangInfo(Map<String, Object> param) {
        Map<String, Object> comMap = tbGsCompanyMapper.queryCompanyParamter(param);
        Object resultObj = null;
        if (MapUtils.isNotEmpty(comMap) && null != comMap.get("paramter")) {
            resultObj = (List) JSONObject.parse(MapUtils.getString(comMap, "paramter"));
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
        }};
        List list;
        //股东信息
        param.put("paramter", "partner");
        Object obj = this.getGsCompangInfo(param);
        if (null != obj) {
            list = (List) JSONObject.parse(obj.toString());
            ;
            resultMap.put("partner", list.size());
        }
        //主要人员
        param.put("paramter", "personnel");
        obj = this.getGsCompangInfo(param);
        if (null != obj) {
            list = (List) JSONObject.parse(obj.toString());
            ;
            resultMap.put("personnel", list.size());
        }
        //变更记录
        param.put("paramter", "change_record");
        obj = this.getGsCompangInfo(param);
        if (null != obj) {
            list = (List) JSONObject.parse(obj.toString());
            ;
            resultMap.put("changeRecord", list.size());
        }
        //处罚信息
        param.put("paramter", "punish");
        obj = this.getGsCompangInfo(param);
        if (MyStringUtils.isNotNull(obj)) {
            list = (List) JSONObject.parse(obj.toString());
            resultMap.put("punish", list.size());
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
        return tbCompanyReportMapper.queryReportYearsForCompany(param);
    }

    /**
     * 获取年份
     *
     * @param param
     * @return
     */
    public Map<String, Object> getReportDetail(Map<String, Object> param) {
        String report = tbCompanyReportMapper.queryReportDetailForCompany(param);
        if (StringUtils.isNotEmpty(report)) {
            return (Map<String, Object>)JSONObject.parse(report);
        }
        return new HashedMap();
    }
}
