package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.model.TbCompany;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangxiahui on 18/4/4.
 */
@Component
public class TbCompanyService {

    Logger logger = Logger.getLogger(TbCompanyService.class);

    @Autowired
    TbCompanyMapper tbCompanyMapper;


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



}
