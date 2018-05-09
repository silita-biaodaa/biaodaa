package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbPersonProjectMapper;
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

    private static ProjectAnalysisUtil analysisUtil = new ProjectAnalysisUtil();

    public TbProjectZhaotoubiao getProjectZhaotoubiaoDetail(Map<String,Object> param){
        TbProjectZhaotoubiao zhaotoubiao = tbProjectZhaotoubiaoMapper.queryZhaobiaoDetailByProId(param);
        if (null == zhaotoubiao || null == zhaotoubiao.getPkid()){
            Map<String,Object> zhaoMap = tbProjectZhaotoubiaoMapper.queryZhaobiaoDetailByBuild(param);
            if(null != zhaoMap){
                zhaotoubiao = new TbProjectZhaotoubiao();
                zhaotoubiao.setZhongbiaoCompany(zhaoMap.get("bOrg").toString());
                zhaotoubiao.setZhongbiaoCompanyCode(zhaoMap.get("orgCode").toString());
                if(null != zhaoMap.get("bidPrice")){
                    zhaotoubiao.setZhongbiaoAmount(zhaoMap.get("bidPrice").toString());
                }
                if(null != zhaoMap.get("proScope")){
                    zhaotoubiao.setProScope(zhaoMap.get("proScope").toString());
                }
                if(null != zhaoMap.get("bidRemark") && !"未办理中标备案".equals(zhaoMap.get("bidRemark").toString())){
                    String remark = zhaoMap.get("bidRemark").toString();
                    String zhongbiaoDate = remark.substring(analysisUtil.getIndex(remark,"中标日期："),analysisUtil.getIndex(remark,","));
                    zhaotoubiao.setZhongbiaoDate(zhongbiaoDate == null ? null:zhongbiaoDate.substring(analysisUtil.getIndex(zhongbiaoDate,"：")+1,zhongbiaoDate.length()));
                    zhaotoubiao.setZhongbiaoDate(zhongbiaoDate);
                }
            }
        }
        if(null == zhaotoubiao){
            zhaotoubiao = new TbProjectZhaotoubiao();
        }
        //获取管理者姓名
        Map<String,Object> zhaoParam = new HashMap<>();
        zhaoParam.put("proId",param.get("proId"));
        zhaoParam.put("company",param.get("company"));
        zhaoParam.put("role","项目经理");
        zhaoParam.put("roleOne","总监理工程师");
        zhaoParam.put("type","build");
        zhaoParam.put("pid",param.get("pkid"));
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


}
