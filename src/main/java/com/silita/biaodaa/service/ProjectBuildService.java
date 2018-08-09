package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
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
    TbCompanyService tbCompanyService;
    @Autowired
    TbProjectCompanyMapper tbProjectCompanyMapper;

    private static ProjectAnalysisUtil analysisUtil = new ProjectAnalysisUtil();

    public TbProjectBuild getProjectDetail(Map<String, Object> param) {
        String pkid = MapUtils.getString(param, "pkid");
        String proId = MapUtils.getString(param, "proId");
        TbProjectBuild projectBuild = tbProjectBuildMapper.queryProjectBuildDetail(proId, pkid);
        Map<String, Object> perMap = new HashMap<String, Object>();
        perMap.put("pid", pkid);
        perMap.put("role", "项目经理");
        perMap.put("roleOther", "项目总监");
        List<TbPersonProject> personProjectList = tbPersonProjectMapper.queryPersonProjectByPid(perMap);
        if (null != personProjectList && personProjectList.size() > 0) {
            for (TbPersonProject pser : personProjectList) {
                if (null != pser.getRole() && "项目经理".equals(pser.getRole())) {
                    projectBuild.setPmName(pser.getName());
                    projectBuild.setPmIdCard(pser.getIdCard());
                } else if (null != pser.getRole() && "项目总监".equals(pser.getRole())) {
                    projectBuild.setPdName(pser.getName());
                    projectBuild.setPdIdCard(pser.getIdCard());
                }
            }
        }

        //TODO: 企业信息
        Map<String, Object> proMap = new HashMap<>();
        proMap.put("proId", proId);
        proMap.put("pid", pkid);
        proMap.put("type", "build");
        List<TbProjectCompany> projectCompanyList = tbProjectCompanyMapper.queryProComList(proMap);
        projectBuild.setCompanys(projectCompanyList);
        return projectBuild;
    }

    private String getIdCard(TbPersonProject pser, String tabCode) {
        List<TbPersonProject> perList = new ArrayList<>();
        List<Map<String, Object>> resutMapList = null;
        String idCard = null;
        if (null != pser) {
            if (null != pser.getInnerid()) {
                perList.add(pser);
                resutMapList = tbPersonProjectMapper.queryPersonByInnerId(perList, tabCode);
                if (null != resutMapList && resutMapList.size() > 0) {
                    if (null != resutMapList.get(0).get("idCrad")) {
                        idCard = resutMapList.get(0).get("idCrad").toString();
                    }
                }
            }
        }
        return idCard;
    }
}
