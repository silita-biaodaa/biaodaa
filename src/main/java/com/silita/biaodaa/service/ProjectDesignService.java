package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbProjectDesignMapper;
import com.silita.biaodaa.model.TbProjectDesign;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectDesignService {

    @Autowired
    TbProjectDesignMapper tbProjectDesignMapper;

    public TbProjectDesign getProjectDesignDetail(Map<String,Object> param){
        Integer proId = MapUtils.getInteger(param,"proId");
        TbProjectDesign projectDesign = tbProjectDesignMapper.queryProjectDesignDetailByProId(proId);
        projectDesign.setDesignProvince(tbProjectDesignMapper.queryProvinceByName(projectDesign.getRegisAddressDesign()));
        projectDesign.setExploreProvince(tbProjectDesignMapper.queryProvinceByName(projectDesign.getRegisAddressExplore()));
        return projectDesign;
    }
}
