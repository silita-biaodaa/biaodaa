package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.*;
import com.silita.biaodaa.model.*;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
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
    @Autowired
    TbCompanyService tbCompanyService;
    @Autowired
    TbProjectMapper tbProjectMapper;

    private static ProjectAnalysisUtil analysisUtil = new ProjectAnalysisUtil();

    public TbProjectDesign getProjectDesignDetail(Map<String,Object> param){
        Integer pkid = MapUtils.getInteger(param,"pkid");
        Integer proId = MapUtils.getInteger(param,"proId");
        TbProjectDesign projectDesign = tbProjectDesignMapper.queryProjectDesignDetailByPkid(pkid);
        if(null == projectDesign){
            return new TbProjectDesign();
        }
        //获取勘察和设计单位
        if(MyStringUtils.isNotNull(projectDesign.getRegisAddressDesign())){
            String desiginProvince = tbProjectDesignMapper.queryProvinceByName(projectDesign.getRegisAddressDesign());
            if(MyStringUtils.isNull(desiginProvince)){
                desiginProvince = "湖南省";
            }
            projectDesign.setDesignProvince(desiginProvince);
        }
        if(MyStringUtils.isNotNull(projectDesign.getRegisAddressExplore())){
            String exproloreProvince = tbProjectDesignMapper.queryProvinceByName(projectDesign.getRegisAddressExplore());
            if(MyStringUtils.isNull(exproloreProvince)){
                exproloreProvince = "湖南省";
            }
            projectDesign.setExploreProvince(exproloreProvince);
        }
        List<TbPersonProject> personProjectList = new ArrayList<>();
        TbProject project = tbProjectMapper.queryProjectDetail(proId);
        String province = "湖南省";
        if(null != project){
            if(null != project.getProWhere() && project.getProWhere().equals("省")){
                province = project.getProWhere().substring(0,analysisUtil.getIndex(project.getProWhere(),"省")+1);
            }
        }
        String tabCode = tbCompanyService.getProvinceCode(province);
        //获取该项目下的施工
//        List<TbProjectBuild> buildList = tbProjectBuildMapper.queryProjectBuildByProId(proId);
//        if (null != buildList && buildList.size() > 0){
//            personProjectList.addAll(this.getPersonList(buildList));
//        }
        //获取该项目下的监理
//        List<TbProjectSupervision> projectSupervisionList = tbProjectSupervisionMapper.queryProjectSupervisionListByProId(proId);
//        if(null != personProjectList && projectSupervisionList.size() > 0){
//            personProjectList.addAll(this.getPersonList(projectSupervisionList));
//        }
        //获取项目下的勘察和设计
        List<Map<String,Object>> pkidList = new ArrayList<>();
        Map<String,Object> pkMap = new HashMap<>();
        pkMap.put("pkid",pkid);
        pkidList.add(pkMap);
        personProjectList = this.getPersonList(pkidList,tabCode);
//        List<TbProjectDesign> projectDesignList = tbProjectDesignMapper.queryProjectDesignDetailByProId(proId);
//        if(null != projectDesignList && projectDesignList.size() > 0){
//            personProjectList.addAll(this.getPersonList(projectDesignList));
//        }
        //获取招投标
//        List<TbProjectZhaotoubiao> projectZhaotoubiaoList = tbProjectZhaotoubiaoMapper.queryZhaotoubiaoListByProId(proId);
//        if(null != projectZhaotoubiaoList && projectZhaotoubiaoList.size() > 0){
//            personProjectList.addAll(this.getPersonList(projectZhaotoubiaoList));
//        }
        projectDesign.setPersonList(personProjectList);
        return projectDesign;
    }


    private List<TbPersonProject> getPersonList (List list,String tabCode){
        List<TbPersonProject> personProjectList = new ArrayList<>();
        List<TbPersonProject> person = tbPersonProjectMapper.queryInnerIdByPkid(list);
        if(null != person && person.size() > 0){
            List<Map<String,Object>> resultMapList  = tbPersonProjectMapper.queryPersonByInnerId(person,tabCode);
            for(TbPersonProject per : person){
                for(Map<String,Object> map : resultMapList){
                    if(null != map.get("innerid") && map.get("innerid").toString().equals(per.getInnerid())){
                        if (null != map && null != map.get("idCard")){
                            per.setIdCard(map.get("idCard").toString());
                        }
                        if(per != map && null != map.get("sealNo")){
                            per.setSealNo(map.get("sealNo").toString());
                        }
//                        personProjectList.add(per);
//                        break;
                    }
                }

            }
        }
        return person;
    }
}
