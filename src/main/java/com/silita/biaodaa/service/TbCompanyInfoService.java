package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCompanyInfoMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyInfo;
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
                    info.setPhone(tbCompanyService.solPhone(info.getPhone()));
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
}
