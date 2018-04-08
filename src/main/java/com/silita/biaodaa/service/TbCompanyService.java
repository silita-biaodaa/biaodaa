package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.dao.TbCompanyQualificationMapper;
import com.silita.biaodaa.model.TbCompany;
import com.silita.biaodaa.model.TbCompanyQualification;
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
                    if(range.length>2){
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



}
