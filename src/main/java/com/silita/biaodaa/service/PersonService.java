package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.event.ListDataEvent;
import java.util.*;

/**
 * 人员信息Service
 * zhushuai
 */
@Service
public class PersonService {

    @Autowired
    TbPersonMapper tbPersonMapper;
    @Autowired
    TbPersonProjectMapper tbPersonProjectMapper;
    @Autowired
    CertUndesirableMapper certUndesirableMapper;
    @Autowired
    TbPersonChangeMapper tbPersonChangeMapper;
    @Autowired
    TbProjectMapper tbProjectMapper;
    @Autowired
    TbProjectCompanyMapper tbProjectCompanyMapper;
    @Autowired
    TbProjectZhaotoubiaoMapper tbProjectZhaotoubiaoMapper;

    /**
     * 获取人员详情
     * created by zhushuai
     *
     * @param param
     * @return
     */
    public Map<String, Object> getPersonDetail(Map<String, Object> param) {
        Map<String, Object> person = new HashMap<>();
        person.put("name", MapUtils.getString(param, "name"));
        person.put("sex", MapUtils.getString(param, "sex"));
        String tabType = MapUtils.getString(param, "tabType");
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("person", person);
        if ("registerCert".equals(tabType)) {
            param.put("type", 1);
            param.remove("certNo");
            List<TbPersonQualification> tbPersonQualificationList = tbPersonMapper.queryPersonDetailByParam(param);
            resultMap.put("personQualificat", tbPersonQualificationList);
            return resultMap;
        } else if ("otherCert".equals(tabType)) {
            param.remove("certNo");
            param.put("type", 2);
            List<TbPersonQualification> tbPersonQualificationList = tbPersonMapper.queryPersonDetailByParam(param);
            resultMap.put("personQualificat", tbPersonQualificationList);
            return resultMap;
        } else if ("personProject".equals(tabType)) {
            List<Map<String, Object>> personProjectList = this.getPersonProjectList(param);
            resultMap.put("prosonProjectList", personProjectList);
            if (null != personProjectList && personProjectList.size() > 0) {
                resultMap.put("total", personProjectList.size());
            }
            return resultMap;
        } else if ("bearCase".equals(tabType)) {
            resultMap.put("bearCaseList", null);
            return resultMap;
        } else if ("badRecord".equals(tabType)) {
            resultMap.put("badRecord", certUndesirableMapper.queryCertUndesinList(param));
            return resultMap;
        } else if ("changeRecord".equals(tabType)) {
            resultMap.put("changeRecord", getPersonChangeList(param));
            return resultMap;
        }
        return null;
    }

    public List<Map<String, Object>> getPersonProjectList(Map<String, Object> param) {
        param.remove("comName");
        String innerId = null;
        List<TbPersonProject> personProjectList = null;
        if (null != param.get("innerid")) {
            innerId = MapUtils.getString(param, "innerid");
            personProjectList = tbPersonProjectMapper.queryPersonProjectByInnerid(innerId);
        }
        if (null == personProjectList || personProjectList.size() == 0) {
            personProjectList = tbPersonProjectMapper.queryPersonProjectByParam(param);
        }
        List<Map<String, Object>> persProjectList = new ArrayList<>();
        Map<String, Object> perProjectMap = null;
        Map<String, Object> paraMap = new HashMap<>();
        TbProjectCompany projectCompany = null;
        List<TbProjectCompany> projectCompanyList = null;
        if (null != personProjectList && personProjectList.size() > 0) {
            for (TbPersonProject per : personProjectList) {
                perProjectMap = new HashMap<>();
                TbProject project = tbProjectMapper.queryProjectDetail(per.getProId());
                perProjectMap.put("proId", project.getProId());
                perProjectMap.put("proType", project.getProType());
                perProjectMap.put("proName", project.getProName());
                perProjectMap.put("proWhere", project.getProWhere());
                perProjectMap.put("role", per.getRole());
                if (MyStringUtils.isNotNull(per.getComName())) {
                    perProjectMap.put("company", per.getComName());
                    perProjectMap.put("bOrg", per.getComName());
                    persProjectList.add(perProjectMap);
                    continue;
                }
                if (MyStringUtils.isNull(per.getComName())) {
                    //判断是否招投标
                    if ("zhaotoubiao".equals(per.getType())) {
                        TbProjectZhaotoubiao zhaotoubiao = tbProjectZhaotoubiaoMapper.queryZhaotouDetailByPkid(per.getPid());
                        if(null != zhaotoubiao){
                            perProjectMap.put("company", zhaotoubiao.getZhongbiaoCompany());
                            perProjectMap.put("bOrg", zhaotoubiao.getZhongbiaoCompany());
                            persProjectList.add(perProjectMap);
                            continue;
                        }
                    }
                    List<String> roleList = new ArrayList<>();
                    if ("项目经理".equals(per.getRole())) {
                        paraMap.put("proId", per.getProId());
                        paraMap.put("pid", per.getPid());
                        roleList.add("施工单位");
                        paraMap.put("roleList", roleList);
                        projectCompanyList = tbProjectCompanyMapper.queryProComList(paraMap);
                        if (null != projectCompanyList && projectCompanyList.size() > 0) {
                            projectCompany = projectCompanyList.get(0);
                        }
                        if (null != projectCompany) {
                            perProjectMap.put("company", projectCompany.getComName());
                            perProjectMap.put("bOrg", projectCompany.getComName());
                        }
                        persProjectList.add(perProjectMap);
                        continue;
                    } else if ("总监理工程师".equals(per.getRole()) || "项目总监".equals(per.getRole())) {
                        paraMap.put("proId", per.getProId());
                        paraMap.put("pid", per.getPid());
                        roleList.add("监理单位");
                        paraMap.put("roleList", roleList);
                        projectCompanyList = tbProjectCompanyMapper.queryProComList(paraMap);
                        if (null != projectCompanyList && projectCompanyList.size() > 0) {
                            projectCompany = projectCompanyList.get(0);
                        }
                        if (null != projectCompany) {
                            perProjectMap.put("bOrg", projectCompany.getComName());
                            perProjectMap.put("company", projectCompany.getComName());
                        }
                        persProjectList.add(perProjectMap);
                        continue;
                    }
                }
            }
        }
        if (null != persProjectList && persProjectList.size() > 0) {
            for (int i = 0; i < persProjectList.size() - 1; i++) {
                for (int j = persProjectList.size() - 1; j > i; j--) {
                    if (persProjectList.get(j).get("proId").toString().equals(persProjectList.get(i).get("proId").toString())) {
                        persProjectList.remove(j);
                    }
                }
            }
        }
        return persProjectList;
    }

    public List<TbPersonChange> getPersonChangeList(Map<String, Object> param) {
        String flag = MapUtils.getString(param, "name") + "_" + MapUtils.getString(param, "sex")
                + "_" + MapUtils.getString(param, "idCard");
        return tbPersonChangeMapper.queryPersonChangeByFlag(flag);
    }
}
