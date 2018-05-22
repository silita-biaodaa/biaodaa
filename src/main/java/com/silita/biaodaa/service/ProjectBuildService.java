package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.TbPersonProject;
import com.silita.biaodaa.model.TbProject;
import com.silita.biaodaa.model.TbProjectBuild;
import com.silita.biaodaa.model.TbProjectDesign;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
import org.apache.commons.collections.MapUtils;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectBuildService {

    @Autowired
    TbProjectBuildMapper tbProjectBuildMapper;
    @Autowired
    TbPersonProjectMapper tbPersonProjectMapper;
    @Autowired
    TbProjectDesignMapper tbProjectDesignMapper;
    @Autowired
    TbProjectSupervisionMapper tbProjectSupervisionMapper;
    @Autowired
    TbCompanyService tbCompanyService;
    @Autowired
    TbProjectMapper tbProjectMapper;

    private static ProjectAnalysisUtil analysisUtil = new ProjectAnalysisUtil();

    public TbProjectBuild getProjectDetail(Map<String,Object> param){
        Integer pkid = MapUtils.getInteger(param,"pkid");
        Integer proId = MapUtils.getInteger(param,"proId");
        TbProject project = tbProjectMapper.queryProjectDetail(proId);
        String province = "湖南省";
        if(null != project){
            if(null != project.getProWhere() && project.getProWhere().equals("省")){
                province = project.getProWhere().substring(0,analysisUtil.getIndex(project.getProWhere(),"省")+1);
            }
        }
        String tabCode = tbCompanyService.getProvinceCode(province);

        TbProjectBuild projectBuild = tbProjectBuildMapper.queryProjectBuildDetail(proId,pkid);
        Map<String, Object> perMap = new HashMap<String,Object>();
        perMap.put("pkid",pkid);
        perMap.put("role","项目经理");
        perMap.put("roleOther","项目总监");
        List<TbPersonProject> personProjectList = tbPersonProjectMapper.queryPersonProjectByPid(perMap);
        if(null != personProjectList && personProjectList.size() > 0){
            for(TbPersonProject pser : personProjectList){
                if(null != pser.getRole() && "项目经理".equals(pser.getRole())){
                    projectBuild.setPmName(pser.getName());
                    projectBuild.setPmIdCard(this.getIdCard(pser,tabCode));
                }else if (null != pser.getRole() && "项目总监".equals(pser.getRole())){
                    projectBuild.setPdName(pser.getName());
                    projectBuild.setPdIdCard(this.getIdCard(pser,tabCode));
                }
            }
        }
        //获取所有企业
        //获取施工单位
//        List<Map<String,Object>> projectBuildCompany = tbProjectBuildMapper.queryProjectBuildCompany(proId);
//        Map<String,Object> map = new HashMap<>();
//        map.put("companyBuild",projectBuildCompany);
        //获取勘察单位
//        List<Map<String,Object>> projectExCompany = tbProjectDesignMapper.queryExProjectDesignCompany(proId);
//        map.put("companyExplore",projectExCompany);
        //获取设计单位
//        List<Map<String,Object>> projectDeCompany = tbProjectDesignMapper.queryDeProjectDesignCompany(proId);
//        map.put("companyDesign",projectDeCompany);
        //获取监理单位
//        List<Map<String,Object>> projectSuvCompany = tbProjectSupervisionMapper.queryProjectSupCompany(proId);
//        map.put("companySuv",projectSuvCompany);
//        projectBuild.setCompanyMap(map);
        return projectBuild;
    }

    private String getIdCard(TbPersonProject pser,String tabCode){
        List<TbPersonProject> perList = new ArrayList<>();
        List<Map<String,Object>> resutMapList = null;
        String idCard = null;
        if(null != pser){
            if(null != pser.getInnerid()){
                perList.add(pser);
                resutMapList =  tbPersonProjectMapper.queryPersonByInnerId(perList,tabCode);
                if(null != resutMapList &&  resutMapList.size() > 0){
                    if(null != resutMapList.get(0).get("idCrad")){
                        idCard =  resutMapList.get(0).get("idCrad").toString();
                    }
                }
            }
        }
        return idCard;
    }
}
