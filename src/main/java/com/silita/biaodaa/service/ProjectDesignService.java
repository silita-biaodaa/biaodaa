package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectDesignService {

    @Autowired
    TbProjectDesignMapper tbProjectDesignMapper;
    @Autowired
    TbPersonProjectMapper tbPersonProjectMapper;
    @Autowired
    TbProjectBuildMapper tbProjectBuildMapper;
    @Autowired
    TbProjectSupervisionMapper tbProjectSupervisionMapper;
    @Autowired
    TbProjectZhaotoubiaoMapper tbProjectZhaotoubiaoMapper;

    public TbProjectDesign getProjectDesignDetail(Map<String,Object> param){
        Integer proId = MapUtils.getInteger(param,"proId");
        TbProjectDesign projectDesign = tbProjectDesignMapper.queryProjectDesignDetailLimitOneByProId(proId);
        if(null == projectDesign){
            return new TbProjectDesign();
        }
        //获取勘察和设计单位
        projectDesign.setDesignOrg(null);
        projectDesign.setExploreOrg(null);
        List<TbProjectDesign> projectDesignList = tbProjectDesignMapper.queryProjectDesignDetailByProId(proId);
        if(null != projectDesignList && projectDesignList.size() > 0){
            for(TbProjectDesign design : projectDesignList){
                if(MyStringUtils.isNotNull(design.getRegisAddressDesign())){
                    String desiginProvince = tbProjectDesignMapper.queryProvinceByName(design.getRegisAddressDesign());
                    if(MyStringUtils.isNull(desiginProvince)){
                        desiginProvince = "湖南省";
                    }
                    design.setDesignProvince(desiginProvince);
                }
                if(MyStringUtils.isNotNull(design.getRegisAddressExplore())){
                    String exproloreProvince = tbProjectDesignMapper.queryProvinceByName(design.getRegisAddressExplore());
                    if(MyStringUtils.isNull(exproloreProvince)){
                        exproloreProvince = "湖南省";
                    }
                    design.setExploreProvince(exproloreProvince);
                }
            }
        }
        projectDesign.setCompanyList(projectDesignList);
        List<TbPersonProject> personProjectList = new ArrayList<>();
        //获取该项目下的施工
        List<TbProjectBuild> buildList = tbProjectBuildMapper.queryProjectBuildByProId(proId);
        if (null != buildList && buildList.size() > 0){
            personProjectList.addAll(this.getPersonList(buildList));
        }
        //获取该项目下的监理
        List<TbProjectSupervision> projectSupervisionList = tbProjectSupervisionMapper.queryProjectSupervisionListByProId(proId);
        if(null != personProjectList && projectSupervisionList.size() > 0){
            personProjectList.addAll(this.getPersonList(projectSupervisionList));
        }
        //获取项目下的勘察和设计
        if(null != projectDesignList && projectDesignList.size() > 0){
            personProjectList.addAll(this.getPersonList(projectDesignList));
        }
        //获取招投标
        List<TbProjectZhaotoubiao> projectZhaotoubiaoList = tbProjectZhaotoubiaoMapper.queryZhaotoubiaoListByProId(proId);
        if(null != projectZhaotoubiaoList && projectZhaotoubiaoList.size() > 0){
            personProjectList.addAll(this.getPersonList(projectZhaotoubiaoList));
        }
        projectDesign.setPersonList(personProjectList);
        return projectDesign;
    }


    private List<TbPersonProject> getPersonList (List list){
        List<TbPersonProject> personProjectList = new ArrayList<>();
        List<TbPersonProject> person = tbPersonProjectMapper.queryInnerIdByPkid(list);
        Map<String,Object> resultMap = null;
        for(TbPersonProject per : person){
            if(null != per.getInnerid()){
                resultMap = tbPersonProjectMapper.queryPersonByInnerId(per.getInnerid());
                if (null != resultMap && null != resultMap.get("idCard")){
                    per.setIdCard(resultMap.get("idCard").toString());
                }
                if(null != resultMap && null != resultMap.get("sealNo")){
                    per.setSealNo(resultMap.get("sealNo").toString());
                }
            }
            personProjectList.add(per);
        }
        return personProjectList;
    }
}
