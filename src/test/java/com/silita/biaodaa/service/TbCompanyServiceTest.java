package com.silita.biaodaa.service;

import com.silita.biaodaa.controller.vo.CompanyQual;
import com.silita.biaodaa.model.TbCompanyQualification;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

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

    @Test
    public void getIndustry(){
        List<Map<String,String>> list =tbCompanyService.getIndustry();
        for (Map<String,String> map : list){
            System.out.println(map.get("name"));
        }
    }

    @Test
    public void queryCompanyQualification(){
        Map<String,List<TbCompanyQualification>> map = tbCompanyService.queryCompanyQualification("31");
        System.out.println("-----");
    }

    @Test
    public void getCompanyReputation(){
        Map<String,Object> map = tbCompanyService.getCompanyReputation("3875");
        System.out.println("-------");
    }
}
