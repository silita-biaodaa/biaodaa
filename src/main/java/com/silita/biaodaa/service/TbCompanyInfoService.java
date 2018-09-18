package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCompanyInfoMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.model.es.CompanyInfoEs;
import com.silita.biaodaa.utils.CommonUtil;
import com.silita.biaodaa.utils.MyStringUtils;
import com.silita.biaodaa.utils.ProjectAnalysisUtil;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TbCompanyInfoService {

    @Autowired
    TbCompanyInfoMapper tbCompanyInfoMapper;
    @Autowired
    TbCompanyMapper tbCompanyMapper;
    @Autowired
    TbCompanyService tbCompanyService;


    public void saveCompanyInfo(List<TbCompanyInfo> comList) {
        Integer count = 0;
        for (TbCompanyInfo info : comList) {
            if (StringUtils.isNotBlank(info.getComName())) {
                count = 0;
                count = tbCompanyInfoMapper.queryCount(info.getComName(), info.getTabCode());
                if (count > 0) {
                    continue;
                }
                tbCompanyInfoMapper.insertCompanyInfo(info);
            }
        }
    }

    public List<TbCompanyInfo> getBranchCompany(Map<String, Object> param) {
        TbCompany company = tbCompanyMapper.getCompany(MapUtils.getString(param, "comId"));
        if (null == company) {
            return new ArrayList<>();
        }
        Map<String, Object> comMap = new HashMap<>();
        comMap.put("comName", company.getComName());
        comMap.put("tabCode", CommonUtil.getCode(company.getRegisAddress()));
        if (company.getRegisAddress().contains("省")) {
            comMap.put("province", company.getRegisAddress().replace("省", ""));
        } else if (company.getRegisAddress().contains("市")) {
            comMap.put("province", company.getRegisAddress().replace("市", ""));
        } else {
            comMap.put("province", company.getRegisAddress());
        }
        List<TbCompanyInfo> companyInfos = tbCompanyInfoMapper.queryBranchCompany(comMap);
        if(null != companyInfos && companyInfos.size() > 0){
            for(TbCompanyInfo info : companyInfos){
                if(MyStringUtils.isNotNull(info.getPhone())){
                    info.setPhone(tbCompanyService.solPhone(info.getPhone(),null));
                }
            }
        }
        return companyInfos;
    }

    public String getProvince(String city) {
        String display = tbCompanyInfoMapper.queryProvinceByCity(city);
        if (StringUtils.isBlank(display)) {
            return null;
        }
        return ProjectAnalysisUtil.getProvince(display);
    }

    /**
     * 去重
     * @param list
     */
    private void deWeight(List<TbCompanyInfo> list){
        for  ( int  i  =   0 ; i  <  list.size()  -   1 ; i ++ )  {
            for  ( int  j  =  list.size()  -   1 ; j  >  i; j -- )  {
                if  (list.get(j).getPkid().equals(list.get(i).getPkid()))  {
                    list.remove(j);
                }
            }
        }
    }

    public List<CompanyInfoEs> esBranchCompany(Map<String, Object> param) {
        Map<String, Object> comMap = new HashMap<>();
        List<Map<String, Object>> provinceList = (List<Map<String, Object>>) MapUtils.getObject(param,"provinceList");
        List<TbCompanyInfo> companyInfos = new ArrayList<>();
        List<TbCompanyInfo> list = new ArrayList();
        if (null != provinceList && provinceList.size() > 0) {
            for (Map<String, Object> provin : provinceList) {
                list = new ArrayList();
                if (!"hunan".equals(provin.get("code").toString())
                        && !"chongq".equals(provin.get("code").toString())
                        && !"liaon".equals(provin.get("code").toString())) {
                    comMap.put("tabCode", provin.get("code").toString());
                }else {
                    comMap.put("tabCode", null);
                }
                comMap.put("comName", MapUtils.getString(param,"comName"));
                list = tbCompanyInfoMapper.queryBranchCompany(comMap);
                if(null != list && list.size() > 0){
                    companyInfos.addAll(list);
                }
            }
        }
        if (null != companyInfos && companyInfos.size() > 0) {
            List<CompanyInfoEs> companyInfoEsList = new ArrayList<>();
            CompanyInfoEs companyInfoEs = null;
            this.deWeight(companyInfos);
            for (TbCompanyInfo info : companyInfos) {
                companyInfoEs = new CompanyInfoEs();
                companyInfoEs.setPkid(info.getPkid());
                companyInfoEs.setComName(info.getComName());
                companyInfoEs.setBusinessNum(info.getBusinessNum());
                companyInfoEs.setCheckDate(info.getCheckDate());
                companyInfoEs.setCheckOrg(info.getCheckOrg());
                companyInfoEs.setCity(info.getCity());
                companyInfoEs.setComAddress(info.getComAddress());
                companyInfoEs.setComType(info.getComType());
                companyInfoEs.setComUrl(info.getComUrl());
                companyInfoEs.setCreditCode(info.getCreditCode());
                companyInfoEs.setEmail(info.getEmail());
                companyInfoEs.setLegalPerson(info.getLegalPerson());
                companyInfoEs.setEndDate(info.getEndDate());
                companyInfoEs.setProvince(info.getProvince());
                companyInfoEs.setRegisCapital(info.getRegisCapital());
                companyInfoEs.setOrgCode(info.getOrgCode());
                companyInfoEs.setScope(info.getScope());
                companyInfoEs.setStartDate(info.getStartDate());
                if (MyStringUtils.isNotNull(info.getPhone())) {
                    companyInfoEs.setPhone(tbCompanyService.solPhone(info.getPhone(), null));
                }
                companyInfoEsList.add(companyInfoEs);
            }
            return companyInfoEsList;
        }
        return null;
    }
}
