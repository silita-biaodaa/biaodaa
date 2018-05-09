package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbPersonProjectMapper;
import com.silita.biaodaa.dao.TbProjectBuildMapper;
import com.silita.biaodaa.dao.TbProjectDesignMapper;
import com.silita.biaodaa.dao.TbProjectSupervisionMapper;
import com.silita.biaodaa.model.TbPersonProject;
import com.silita.biaodaa.model.TbProjectBuild;
import com.silita.biaodaa.model.TbProjectDesign;
import org.apache.commons.collections.MapUtils;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public TbProjectBuild getProjectDetail(Map<String,Object> param){
        Integer pkid = MapUtils.getInteger(param,"pkid");
        Integer proId = MapUtils.getInteger(param,"proId");
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
                    projectBuild.setPmIdCard(this.getIdCard(pser));
                }else if (null != pser.getRole() && "项目总监".equals(pser.getRole())){
                    projectBuild.setPdName(pser.getName());
                    projectBuild.setPdIdCard(this.getIdCard(pser));
                }
            }
        }
        //获取所有企业
        //获取施工单位
        List<Map<String,Object>> projectBuildCompany = tbProjectBuildMapper.queryProjectBuildCompany(proId);
        Map<String,Object> map = new HashMap<>();
        map.put("companyBuild",projectBuildCompany);
        //获取勘察单位
        List<Map<String,Object>> projectExCompany = tbProjectDesignMapper.queryExProjectDesignCompany(proId);
        map.put("companyExplore",projectExCompany);
        //获取设计单位
        List<Map<String,Object>> projectDeCompany = tbProjectDesignMapper.queryDeProjectDesignCompany(proId);
        map.put("companyDesign",projectDeCompany);
        //获取监理单位
        List<Map<String,Object>> projectSuvCompany = tbProjectSupervisionMapper.queryProjectSupCompany(proId);
        map.put("companySuv",projectSuvCompany);
        projectBuild.setCompanyMap(map);
        return projectBuild;
    }

    private String getIdCard(TbPersonProject pser){
        Map<String,Object> resutMap = null;
        String idCard = null;
        if(null != pser){
            if(null != pser.getInnerid()){
                resutMap =  tbPersonProjectMapper.queryPersonByInnerId(pser.getInnerid());
                if(null != resutMap && null != resutMap.get("idCrad")){
                    idCard = resutMap.get("idCrad").toString();
                }
            }
        }
        return idCard;
    }
}
