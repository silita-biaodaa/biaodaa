package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.event.ListDataEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    TbProjectDesignMapper tbProjectDesignMapper;
    @Autowired
    TbProjectSupervisionMapper tbProjectSupervisionMapper;
    @Autowired
    TbProjectBuildMapper tbProjectBuildMapper;
    @Autowired
    CertUndesirableMapper certUndesirableMapper;
    @Autowired
    TbPersonChangeMapper tbPersonChangeMapper;

    /**
     * 获取人员详情
     * created by zhushuai
     * @param param
     * @return
     */
    public Map<String,Object> getPersonDetail(Map<String,Object> param){
        Map<String,Object> person = new HashMap<>();
        person.put("name",MapUtils.getString(param,"name"));
        person.put("sex",MapUtils.getString(param,"sex"));
        String tabType = MapUtils.getString(param,"tabType");
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("person",person);
        if("registerCert".equals(tabType)){
            param.put("type",1);
            param.remove("certNo");
            List<TbPersonQualification> tbPersonQualificationList = tbPersonMapper.queryPersonDetailByParam(param);
            resultMap.put("personQualificat",tbPersonQualificationList);
            return resultMap;
        }else if("otherCert".equals(tabType)){
            param.remove("certNo");
            param.put("type",2);
            List<TbPersonQualification> tbPersonQualificationList = tbPersonMapper.queryPersonDetailByParam(param);
            resultMap.put("personQualificat",tbPersonQualificationList);
            return resultMap;
        }else if("personProject".equals(tabType)){
            List<Map<String,Object>> prosonProjectList = this.getPersonProjectList(param);
            resultMap.put("prosonProjectList",prosonProjectList);
            return resultMap;
        }else if("bearCase".equals(tabType)){
            resultMap.put("bearCaseList",null);
            return resultMap;
        }else if("badRecord".equals(tabType)){
            resultMap.put("badRecord",certUndesirableMapper.queryCertUndesinList(param));
            return resultMap;
        }else if("changeRecord".equals(tabType)){
            resultMap.put("changeRecord",getPersonChangeList(param));
            return resultMap;
        }
        return null;
    }

    public List<Map<String,Object>> getPersonProjectList(Map<String,Object> param){
        param.remove("comName");
        String innerId = null;
        List<TbPersonProject> personProjectList = null;
        if(null != param.get("innerid")){
            innerId = MapUtils.getString(param,"innerid");
            personProjectList = tbPersonProjectMapper.queryPersonProjectByInnerid(innerId);
        }
        if(null == personProjectList){
            personProjectList = tbPersonProjectMapper.queryPersonProjectByParam(param);
        }
        List<Map<String,Object>> persProjectList = new ArrayList<>();
        Map<String,Object> perProjectMap = null;
        if(null != personProjectList && personProjectList.size() > 0){
            for(TbPersonProject per : personProjectList){
                perProjectMap = new HashMap<>();
                perProjectMap.put("role",per.getRole());
                if("supervision".equals(per.getType())){
                    TbProjectSupervision projectSupervision = tbProjectSupervisionMapper.querySupervisionDetailById(per.getPid());
                    if(null != projectSupervision){
                        perProjectMap.put("proType",projectSupervision.getProTypeMain());
                        perProjectMap.put("proName",projectSupervision.getProName());
                        perProjectMap.put("proWhere",projectSupervision.getProWhere());
                        perProjectMap.put("superOrg",projectSupervision.getSuperOrg());
                        perProjectMap.put("proId",projectSupervision.getProId());
                    }
                }else if ("build".equals(per.getType())){
                    TbProjectBuild projectBuild = tbProjectBuildMapper.queryProjectBuildDetail(null,per.getPid());
                    if(null != projectBuild){
                        perProjectMap.put("proType",projectBuild.getProTypeMain());
                        perProjectMap.put("proName",projectBuild.getProName());
                        perProjectMap.put("proWhere",projectBuild.getProWhere());
                        perProjectMap.put("bOrg",projectBuild.getBOrg());
                        perProjectMap.put("proId",projectBuild.getProId());
                    }
                }else if ("design".equals(per.getType())){
                    TbProjectDesign projectDesign = tbProjectDesignMapper.queryProjectDesignById(per.getPid());
                    if(null != projectDesign){
                        perProjectMap.put("proType",projectDesign.getProTypeMain());
                        perProjectMap.put("proName",projectDesign.getProName());
                        perProjectMap.put("proWhere",projectDesign.getProWhere());
                        perProjectMap.put("exploreOrg",projectDesign.getExploreOrg());
                        perProjectMap.put("designOrg",projectDesign.getDesignOrg());
                        perProjectMap.put("proId",projectDesign.getProId());
                    }
                }
                persProjectList.add(perProjectMap);
            }
        }
        return persProjectList;
    }

    public List<TbPersonChange> getPersonChangeList(Map<String,Object> param){
        String flag = MapUtils.getString(param,"name")+"_"+MapUtils.getString(param,"sex")
                +"_"+MapUtils.getString(param,"idCard");
        return tbPersonChangeMapper.queryPersonChangeByFlag(flag);
    }
}
