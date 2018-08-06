package com.silita.biaodaa.service;

import com.silita.biaodaa.controller.vo.CompanyQual;
import com.silita.biaodaa.dao.AptitudeDictionaryMapper;
import com.silita.biaodaa.dao.TbCompanyInfoMapper;
import com.silita.biaodaa.dao.TbZzGradeMapper;
import com.silita.biaodaa.es.ElasticseachService;
import com.silita.biaodaa.model.AptitudeDictionary;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyInfo;
import com.silita.biaodaa.model.TbZzGrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
//@Transactional
public class GradeService {

    @Autowired
    TbZzGradeMapper tbZzGradeMapper;
    @Autowired
    AptitudeDictionaryMapper aptitudeDictionaryMapper;
    @Autowired
    TbCompanyInfoMapper tbCompanyInfoMapper;
    @Autowired
    ElasticseachService elasticseachService;

    public void getZzTpyeList(List<TbZzGrade> list) {
        TbZzGrade tbZzGrade = null;
        Map<String, Object> param = new HashMap<>();
        for (TbZzGrade grade : list) {
            param.put("gradeName", grade.getGradeName());
            param.put("zzId", grade.getZzId());
            tbZzGrade = tbZzGradeMapper.selectDate(param);
            if (null != tbZzGrade) {
                tbZzGradeMapper.updateDate(tbZzGrade.getPkid());
            } else {
                tbZzGradeMapper.insertDate(grade);
            }
        }
    }

    public List<CompanyQual> getQual(Integer type) {
        List<AptitudeDictionary> list = aptitudeDictionaryMapper.queryAptitude();
        Map<String, CompanyQual> map = new HashMap<>();
        List<CompanyQual> companyQualList = new ArrayList<>();
        List<CompanyQual> qualList = null;
        CompanyQual comQual = null;
        for (AptitudeDictionary ap : list) {
            CompanyQual qual = new CompanyQual();
            qual.setName(ap.getMajorname());
            qual.setCode(ap.getMajoruuid());
            qualList = tbZzGradeMapper.quaryList(ap.getId(), type);
            if (null != qualList && qualList.size() > 0) {
                qual.setList(qualList);
            } else {
                qualList = new ArrayList<>();
                comQual = new CompanyQual();
                comQual.setCode(ap.getMajoruuid());
                comQual.setName("全部");
            }
            if (map.get(ap.getAptitudename()) != null) {
                CompanyQual pany = map.get(ap.getAptitudename());
                List<CompanyQual> panyList = pany.getList();
                if (panyList == null) {
                    panyList = new ArrayList<>();
                }
                panyList.add(qual);
            } else {
                CompanyQual pany = new CompanyQual();
                List<CompanyQual> panyList = new ArrayList<>();
                panyList.add(qual);
                pany.setName(ap.getAptitudename());
                pany.setCode(ap.getAptitudecode());
                pany.setList(panyList);
                map.put(ap.getAptitudename(), pany);
            }
        }
        for (CompanyQual companyQual : map.values()) {
            companyQualList.add(companyQual);
        }
        return companyQualList;
    }

    public void updateCompany() {
        Integer count = tbCompanyInfoMapper.queryComCount();
        if (count > 0) {
            Integer pages = elasticseachService.getPage(count, 3000);
            Integer pageSize = 3000;
            Integer page = 0;
            List<TbCompany> companyList = null;
            TbCompanyInfo companyInfo = null;
            for (int i = 1; i <= pages; i++) {
                page = (i - 1) * 3000;
                companyList = tbCompanyInfoMapper.queryCompanyList(page,pageSize);
                if(null != companyList && companyList.size() > 0){
                    for(TbCompany company : companyList){
                        if("湖南耀邦建设有限公司".equals(company.getComName())){
                            System.out.println("湖南耀邦建设有限公司");
                        }
                        companyInfo = tbCompanyInfoMapper.queryDetailByComName(company.getComName(),this.getCode(company.getRegisAddress()));
                        if(null != companyInfo){
                            if(null != companyInfo.getCity()){
                                company.setCity(companyInfo.getCity());
                            }
                            if(null != companyInfo.getRegisCapital()){
                                company.setRegisCapital(companyInfo.getRegisCapital());
                            }
                            tbCompanyInfoMapper.updateCompany(company);
                        }
                    }
                }
            }
        }
    }

    private String getCode(String province) {
        String tabCode = "";
        switch (province) {
            case "广西壮族自治区":
                tabCode = "guangx";
                break;
            case "江西省":
                tabCode = "jiangx";
                break;
            case "贵州省":
                tabCode = "guiz";
                break;
            case "吉林省":
                tabCode = "jil";
                break;
            case "河北省":
                tabCode = "hebei";
                break;
            case "四川省":
                tabCode = "sichuan";
                break;
            case "天津市":
                tabCode = "tianj";
                break;
            case "甘肃省":
                tabCode = "gans";
                break;
            case "黑龙江省":
                tabCode = "heilj";
                break;
            case "青海省":
                tabCode = "qingh";
                break;
            case "西藏自治区":
                tabCode = "xizang";
                break;
            case "安徽省":
                tabCode = "anh";
                break;
            case "北京市":
                tabCode = "beij";
                break;
            case "福建省":
                tabCode = "fuj";
                break;
            case "浙江省":
                tabCode = "zhej";
                break;
            case "河南省":
                tabCode = "henan";
                break;
            case "江苏省":
                tabCode = "jiangs";
                break;
            case "内蒙古自治区":
                tabCode = "neimg";
                break;
            case "宁夏回族自治区":
                tabCode = "ningx";
                break;
            case "山东省":
                tabCode = "shand";
                break;
            case "山西省":
                tabCode = "sanx";
                break;
            case "海南省":
                tabCode = "hain";
                break;
            case "上海市":
                tabCode = "shangh";
                break;
            case "广东省":
                tabCode = "guangd";
                break;
            case "新疆维吾尔自治区":
                tabCode = "xinjiang";
                break;
            case "云南省":
                tabCode = "yunn";
                break;
            case "陕西省":
                tabCode = "shanxi";
                break;
            case "湖北省":
                tabCode = "hubei";
                break;
                default:
                    tabCode = null;
                    break;
        }
        return tabCode;
    }
}
