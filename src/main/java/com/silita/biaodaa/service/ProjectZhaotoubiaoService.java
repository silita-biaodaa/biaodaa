package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbPersonProjectMapper;
import com.silita.biaodaa.dao.TbProjectSupervisionMapper;
import com.silita.biaodaa.dao.TbProjectZhaotoubiaoMapper;
import com.silita.biaodaa.model.TbPersonProject;
import com.silita.biaodaa.model.TbProjectZhaotoubiao;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private static ProjectAnalysisUtil analysisUtil = new ProjectAnalysisUtil();

    public TbProjectZhaotoubiao getProjectZhaotoubiaoDetail(Map<String,Object> param){
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
                    zhaotoubiao = this.getProjectZhao(zhaoMap);
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
//        zhaoParam.put("type","build");
        zhaoParam.put("pkid",param.get("pkid"));
        List<TbPersonProject> personProjectList = tbPersonProjectMapper.queryPersonProject(zhaoParam);
        if((null != personProjectList && personProjectList.size() > 0) && null != personProjectList.get(0)){
            if (null != personProjectList.get(0).getInnerid()){
                Map<String,Object> cardMap = tbPersonProjectMapper.queryPersonByInnerId(personProjectList.get(0).getInnerid());
                if (null != cardMap && null != cardMap.get("idCard")){
                    zhaotoubiao.setIdCard(cardMap.get("idCard").toString());
                }
            }
            zhaotoubiao.setName(personProjectList.get(0).getName());
        }

        //获取该项目下施工的所有人员
        zhaoParam.remove("role");
        zhaoParam.remove("roleOne");
        List<TbPersonProject> personProjectAllList = tbPersonProjectMapper.queryPersonProject(zhaoParam);
        if(null != personProjectAllList && personProjectAllList.size() >0){
            for (TbPersonProject personProject : personProjectAllList){
                if(null != personProject.getInnerid()){
                    Map<String,Object> cardMap = tbPersonProjectMapper.queryPersonByInnerId(personProject.getInnerid());
                    if (null != cardMap && null != cardMap.get("idCard")){
                        personProject.setIdCard(cardMap.get("idCard").toString());
                    }
                }
            }
        }
        zhaotoubiao.setPersonList(personProjectAllList);
        return zhaotoubiao;
    }


    private TbProjectZhaotoubiao getProjectZhao(Map<String,Object> map){
        TbProjectZhaotoubiao zhaotoubiao = new TbProjectZhaotoubiao();
        zhaotoubiao.setZhongbiaoCompany(map.get("bOrg").toString());
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
                String amount = remark.substring(analysisUtil.getIndex(remark,"中标价格：")+5,analysisUtil.getIndex(remark,"万元"));
                zhaotoubiao.setZhongbiaoAmount(amount == null ? null : amount);
            }
        }
        return zhaotoubiao;
    }
}
