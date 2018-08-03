package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCompanyInfoMapper;
import com.silita.biaodaa.model.TbCompanyInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TbCompanyInfoService {

    @Autowired
    TbCompanyInfoMapper tbCompanyInfoMapper;


    public void saveCompanyInfo(List<TbCompanyInfo> comList) {
        TbCompanyInfo companyInfo = null;
        for (TbCompanyInfo info : comList) {
            if(StringUtils.isNotBlank(info.getComName())){
                companyInfo = null;
                companyInfo = tbCompanyInfoMapper.queryDetailByComName(info.getComName());
                if (null == companyInfo) {
                    tbCompanyInfoMapper.insertCompanyInfo(info);
                }
            }
        }
    }
}
