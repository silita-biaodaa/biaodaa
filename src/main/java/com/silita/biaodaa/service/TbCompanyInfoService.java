package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCompanyInfoMapper;
import com.silita.biaodaa.model.TbCompanyInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TbCompanyInfoService {

    @Autowired
    TbCompanyInfoMapper tbCompanyInfoMapper;


    public void saveCompanyInfo(List<TbCompanyInfo> comList){
        for(TbCompanyInfo info : comList){
            tbCompanyInfoMapper.insertCompanyInfo(info);
        }
    }
}
