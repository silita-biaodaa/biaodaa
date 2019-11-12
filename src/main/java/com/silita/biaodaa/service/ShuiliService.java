package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbProjectShuiliMapper;
import com.silita.biaodaa.model.TbProjectShuili;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ShuiliService {
    @Autowired
    TbProjectShuiliMapper tbProjectShuiliMapper;

    /**
     * 获取项目类型
     * @return
     */
    public Map<String,Object> getProType(){
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("proStatus",tbProjectShuiliMapper.queryShuiliProType());
        return resultMap;
    }
}
