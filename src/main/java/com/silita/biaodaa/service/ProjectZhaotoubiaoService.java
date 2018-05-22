package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbPersonProjectMapper;
import com.silita.biaodaa.dao.TbProjectMapper;
import com.silita.biaodaa.dao.TbProjectSupervisionMapper;
import com.silita.biaodaa.dao.TbProjectZhaotoubiaoMapper;
import com.silita.biaodaa.model.TbPersonProject;
import com.silita.biaodaa.model.TbProject;
import com.silita.biaodaa.model.TbProjectZhaotoubiao;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectZhaotoubiaoService {

    @Autowired
    TbProjectZhaotoubiaoMapper tbProjectZhaotoubiaoMapper;
    @Autowired
    TbPersonProjectMapper tbPersonProjectMapper;
    @Autowired
    TbProjectSupervisionMapper tbProjectSupervisionMapper;
    @Autowired
    TbCompanyService tbCompanyService;
    @Autowired
    TbProjectMapper tbProjectMapper;

    private static ProjectAnalysisUtil analysisUtil = new ProjectAnalysisUtil();

    public TbProjectZhaotoubiao getProjectZhaotoubiaoDetail(Map<String,Object> param){
        TbProject project = tbProjectMapper.queryProjectDetail(Integer.valueOf(param.get("proId").toString()));
        String province = "湖南省";
        if(null != project){
            if(null != project.getProWhere() && project.getProWhere().equals("省")){
                province = project.getProWhere().substring(0,analysisUtil.getIndex(project.getProWhere(),"省")+1);
            }
        }
        String tabCode = tbCompanyService.getProvinceCode(province);
        TbProjectZhaotoubiao zhaotoubiao = tbProjectZhaotoubiaoMapper.queryZhaobiaoDetailByProId(param);
        if (null == zhaotoubiao || null == zhaotoubiao.getPkid()){
            Map<String,Object> zhaoMap = tbProjectZhaotoubiaoMapper.queryZhaobiaoDetailByBuild(param);
            if(null != zhaoMap){
                zhaoMap.put("bidType","施工");
                zhaotoubiao = this.getProjectZhao(zhaoMap);
            }else {
                Map<String,Object> zhaoSuMap = tbProjectSupervisionMapper.querySuperDetail(param);
                if(null != zhaoSuMap){
                    zhaoSuMap.put("bidType","监理");
                    zhaotoubiao = this.getProjectZhao(zhaoSuMap);
                }
            }
        }
        if(null == zhaotoubiao){
            zhaotoubiao = new TbProjectZhaotoubiao();
        }
        //获取管理者姓名
        Map<String,Object> zhaoParam = new HashMap<>();
        zhaoParam.put("proId",param.get("proId"));
//        zhaoParam.put("company",param.get("company"));
        zhaoParam.put("role","项目经理");
        zhaoParam.put("roleOne","总监理工程师");
        if("监理".equals(param.get("zhaobiaoType").toString())){
            zhaoParam.put("type","supervision");
        }else {
            zhaoParam.put("type","build");
        }
        zhaoParam.put("pkid",param.get("pkid"));
        List<TbPersonProject> personProjectList = tbPersonProjectMapper.queryPersonProject(zhaoParam);
        List<TbPersonProject> paramPersonList = new ArrayList<>();
        if(null != personProjectList && personProjectList.size() > 0){
            for (TbPersonProject personProject : personProjectList){
                if(!"已离岗".equals(personProject.getStatus())){
                    paramPersonList.add(personProject);
                    break;
                }else{
                    paramPersonList.add(personProject);
                    break;
                }
            }
        }
        if((null != paramPersonList && paramPersonList.size() > 0)){
            if (null != paramPersonList.get(0).getInnerid()){
                List<Map<String,Object>> list = tbPersonProjectMapper.queryPersonByInnerId(paramPersonList,tabCode);
                if(null != list && list.size() > 0){
                    Map<String,Object> cardMap = list.get(0);
                    if (null != cardMap && null != cardMap.get("idCard")){
                        zhaotoubiao.setIdCard(cardMap.get("idCard").toString());
                    }
                }
            }
            zhaotoubiao.setName(paramPersonList.get(0).getName());
        }

        //获取该项目下施工的所有人员
        zhaoParam.remove("role");
        zhaoParam.remove("roleOne");
        List<TbPersonProject> personProjectAllList = tbPersonProjectMapper.queryPersonByParam(zhaoParam);
        if(null != personProjectAllList && personProjectAllList.size() >0){
            List<Map<String,Object>> cardList = tbPersonProjectMapper.queryPersonByInnerId(personProjectAllList,tabCode);
            if(null != cardList && cardList.size() > 0){
                for (TbPersonProject personProject : personProjectAllList){
                    for(Map<String,Object> map : cardList){
                        if(null != map.get("innerid") && map.get("innerid").toString().equals(personProject.getInnerid())){
                            if (null != map && null != map.get("idCard")){
                                personProject.setIdCard(map.get("idCard").toString());
                                break;
                            }
                        }
                    }
                }
            }
        }
        zhaotoubiao.setPersonList(personProjectAllList);
        return zhaotoubiao;
    }


    private TbProjectZhaotoubiao getProjectZhao(Map<String,Object> map){
        TbProjectZhaotoubiao zhaotoubiao = new TbProjectZhaotoubiao();
        if(null != map.get("bOrg")){
            zhaotoubiao.setZhongbiaoCompany(map.get("bOrg").toString());
        }
        if(null != map.get("orgCode")){
            zhaotoubiao.setZhongbiaoCompanyCode(map.get("orgCode").toString());
        }
        if(null != map.get("acreage")){
            zhaotoubiao.setAcreage(map.get("acreage").toString());
        }
        zhaotoubiao.setZhaobiaoType(map.get("bidType").toString());
        if(null != map.get("bidPrice")){
            zhaotoubiao.setZhongbiaoAmount(map.get("bidPrice").toString());
        }
        if(null != map.get("proScope")){
            zhaotoubiao.setProScope(map.get("proScope").toString());
        }
        if(null != map.get("bidRemark") && !"未办理中标备案".equals(map.get("bidRemark").toString())){
            String remark = map.get("bidRemark").toString();
            String zhongbiaoDate = remark.substring("中标日期：".length(),analysisUtil.getIndex(remark,","));
            zhaotoubiao.setZhongbiaoDate(zhongbiaoDate == null ? null:zhongbiaoDate);
            zhaotoubiao.setZhongbiaoDate(zhongbiaoDate);
            if(null == zhaotoubiao.getZhongbiaoAmount()){
                String amount = remark.substring(analysisUtil.getIndex(remark,"中标价格："),analysisUtil.getIndex(remark,"万元"));
                zhaotoubiao.setZhongbiaoAmount(isNumeric(amount) == true ? amount.substring(amount.lastIndexOf("中标价格：")+"中标价格：".length(),amount.length()) : null);
            }
        }
        return zhaotoubiao;
    }


    public static boolean isNumeric(String str){
        for (int i =  str.length(); --i>=0;) {
            if(Character.isDigit(str.charAt(i))){
                return true;
            }
        }
        return false;
    }
}
