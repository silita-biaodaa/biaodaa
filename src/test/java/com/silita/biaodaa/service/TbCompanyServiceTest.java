package com.silita.biaodaa.service;

import com.silita.biaodaa.controller.vo.CompanyQual;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by zhangxiahui on 18/4/10.
 */
public class TbCompanyServiceTest extends ConfigTest {

    @Autowired
    TbCompanyService tbCompanyService;


    @Test
    public void getCompanyQual(){
        List<CompanyQual> list = tbCompanyService.getCompanyQual();
        for(CompanyQual companyQual : list){
            System.out.println(companyQual.getName());
        }
    }
}
