package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.dao.TbProjectCompanyMapper;
import com.silita.biaodaa.dao.TbProjectContractMapper;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbProjectCompany;
import com.silita.biaodaa.model.TbProjectContract;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProjectContractService {

    @Autowired
    TbProjectContractMapper tbProjectContractMapper;
    @Autowired
    TbProjectCompanyMapper tbProjectCompanyMapper;
    @Autowired
    TbCompanyMapper tbCompanyMapper;

    /**
     * 获取合同备案详情
     *
     * @param param
     * @return
     */
    public TbProjectContract getProjectContractDetail(Map<String, Object> param) {
        TbProjectContract tbProjectContract = tbProjectContractMapper.queryProjectContractDetail(param);
        if(null == tbProjectContract){
            return tbProjectContract;
        }
        Map<String,Object> comMap = new HashMap<>();
        comMap.put("proId",MapUtils.getString(param,"proId"));
        comMap.put("pid",MapUtils.getString(param,"pkid"));
        comMap.put("type","contract");
        List<String> roleList = new ArrayList<>();
        roleList.add("发包单位");
        roleList.add("承包单位");
        roleList.add("联合体承包单位");
        comMap.put("roleList",roleList);
        List<TbProjectCompany> projectCompanyList = tbProjectCompanyMapper.queryProComList(comMap);
        if(null != projectCompanyList && projectCompanyList.size() > 0){
            TbCompany tbCompany = null;
            for(TbProjectCompany company : projectCompanyList){
                tbCompany = tbCompanyMapper.queryCompanyDetail(company.getComName());
                if("发包单位".equals(company.getRole())){
                    tbProjectContract.setLetContractComName(company.getComName());
                    if(null != company){
                        tbProjectContract.setLetOrgCode(company.getOrgCode());
                    }
                }else if("承包单位".equals(company.getRole())){
                    tbProjectContract.setContractComName(company.getComName());
                    if(null != company){
                        tbProjectContract.setContractOrgCode(company.getOrgCode());
                    }
                }else if("联合体承包单位".equals(company.getRole())){
                    tbProjectContract.setJointComName(company.getComName());
                    if(null != company){
                        tbProjectContract.setJointOrgCode(company.getOrgCode());
                    }
                }
            }
        }
        return tbProjectContract;
    }
}
