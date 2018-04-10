package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.CompanyQual;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.AptitudeDictionaryMapper;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.dao.TbCompanyQualificationMapper;
import com.silita.biaodaa.dao.TbPersonQualificationMapper;
import com.silita.biaodaa.model.AptitudeDictionary;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyQualification;
import com.silita.biaodaa.model.TbPersonQualification;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by zhangxiahui on 18/4/4.
 */
@Component
public class TbCompanyService {

    Logger logger = Logger.getLogger(TbCompanyService.class);

    @Autowired
    TbCompanyMapper tbCompanyMapper;

    @Autowired
    TbCompanyQualificationMapper tbCompanyQualificationMapper;

    @Autowired
    TbPersonQualificationMapper tbPersonQualificationMapper;

    @Autowired
    AptitudeDictionaryMapper aptitudeDictionaryMapper;


    public PageInfo queryCompanyList(Page page,String keyWord){
        List<TbCompany> list = new ArrayList<>();

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        list = tbCompanyMapper.queryCompanyList(keyWord);
        PageInfo pageInfo = new PageInfo(list);

        return pageInfo;
    }

    public TbCompany getCompany(Integer comId){
        return tbCompanyMapper.getCompany(comId);
    }

    public Map<String,List<TbCompanyQualification>> queryCompanyQualification(Integer comId){
        Map<String,List<TbCompanyQualification>> qualMap = new HashMap<>();
        if(comId!=null){
            List<TbCompanyQualification> list = tbCompanyQualificationMapper.queryCompanyQualification(comId);
            for(TbCompanyQualification qual : list){
                if(qual.getQualType()!=null&&qual.getRange()!=null){
                    String key = qual.getQualType();
                    List<TbCompanyQualification> qualList;
                    if(qualMap.get(key)!=null){
                        qualList = qualMap.get(key);
                    }else{
                        qualList = new ArrayList<>();

                    }
                    String [] range = qual.getRange().split("；");
                    if(range.length>=2){
                        for(int i = 0;i<range.length;i++){
                            TbCompanyQualification qualT = qual.clon();
                            qualT.setQualName(range[i]);
                            qualList.add(qualT);
                        }
                    }else{
                        qual.setQualName(range[0]);
                        qualList.add(qual);
                    }
                    qualMap.put(key,qualList);
                }
            }

        }
        return qualMap;
    }

    public List<Map<String,Object>> queryQualList(Integer comId){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,List<TbCompanyQualification>> qualMap = queryCompanyQualification(comId);
        Set<String> set = qualMap.keySet();
        for(String key : set){
            Map<String,Object> map = new HashMap<>();
            map.put("qualType",key);
            map.put("list",qualMap.get(key));
            list.add(map);
        }
        return list;
    }

    public List<Map<String,Object>> getCompanyPersonCate(Integer comId){
        return tbPersonQualificationMapper.getCompanyPersonCate(comId);
    }

    public PageInfo queryCompanyPerson(Page page,Map<String,Object> param){
        List<TbPersonQualification> list = new ArrayList<>();
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        list = tbPersonQualificationMapper.queryCompanyPerson(param);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    public List<CompanyQual> getCompanyQual(){
        List<AptitudeDictionary> list = aptitudeDictionaryMapper.queryAptitude();
        Map<String,CompanyQual> map = new HashMap<>();
        List<CompanyQual> companyQualList = new ArrayList<>();
        for(AptitudeDictionary ap : list){
            CompanyQual qual = new CompanyQual();
            qual.setName(ap.getMajorname());
            qual.setCode(ap.getMajoruuid());
            qual.setList(getZzTpyeList(ap.getMajoruuid(),ap.getZztype()));
            if(map.get(ap.getAptitudename())!=null){
                CompanyQual pany = map.get(ap.getAptitudename());
                List<CompanyQual> panyList = pany.getList();
                if(panyList==null){
                    panyList = new ArrayList<>();
                }
                panyList.add(qual);
            }else{
                CompanyQual pany = new CompanyQual();
                List<CompanyQual> panyList = new ArrayList<>();
                panyList.add(qual);
                pany.setName(ap.getAptitudename());
                pany.setCode(ap.getAptitudecode());
                pany.setList(panyList);
                map.put(ap.getAptitudename(),pany);
            }
        }
        for(CompanyQual companyQual : map.values()){
            companyQualList.add(companyQual);
        }
        return companyQualList;
    }

    public List<CompanyQual> getZzTpyeList(String majoruuid,String zzType){
        List<CompanyQual> list = new ArrayList<>();
        if("1".equals(zzType)){
            //等级：0=特级，1=一级，2=二级，11=一级及以上，21=二级及以上，31=三级及以上，-1=甲级，-2=乙级，-3=丙级
            CompanyQual qual0 = new CompanyQual();
            qual0.setName("特级");
            qual0.setCode(majoruuid+"/"+"0");

            CompanyQual qual1 = new CompanyQual();
            qual1.setName("一级");
            qual1.setCode(majoruuid+"/"+"1");

            CompanyQual qual2 = new CompanyQual();
            qual2.setName("二级");
            qual2.setCode(majoruuid+"/"+"2");

            CompanyQual qual3 = new CompanyQual();
            qual3.setName("三级");
            qual3.setCode(majoruuid+"/"+"3");

            CompanyQual qual11 = new CompanyQual();
            qual11.setName("一级及以上");
            qual11.setCode(majoruuid+"/"+"11");

            CompanyQual qual21 = new CompanyQual();
            qual21.setName("二级及以上");
            qual21.setCode(majoruuid+"/"+"21");

            CompanyQual qual31 = new CompanyQual();
            qual31.setName("三级及以上");
            qual31.setCode(majoruuid+"/"+"31");

            list.add(qual0);
            list.add(qual1);
            list.add(qual2);
            list.add(qual3);
            list.add(qual11);
            list.add(qual21);
            list.add(qual31);
        }else if("2".equals(zzType)){
            CompanyQual qual1 = new CompanyQual();
            qual1.setName("甲级");
            qual1.setCode(majoruuid+"/"+"-1");

            CompanyQual qual2 = new CompanyQual();
            qual2.setName("乙级");
            qual2.setCode(majoruuid+"/"+"-2");

            CompanyQual qual3 = new CompanyQual();
            qual3.setName("丙级");
            qual3.setCode(majoruuid+"/"+"-3");

            list.add(qual1);
            list.add(qual2);
            list.add(qual3);
        }
        return list;
    }

    
    public List<Map<String,String>> getIndustry(){
        return aptitudeDictionaryMapper.getIndustry();
    }




}
