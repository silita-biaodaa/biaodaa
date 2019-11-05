package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbProjectTrafficMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公路
 */
@Service
public class HighwayService {
    @Autowired
    TbProjectTrafficMapper tbProjectTrafficMapper;

    public Map<String,Object> getBuildInProType(){
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("build",getBuildGroup());
        resultMap.put("proType",getProjectType());
        return resultMap;
    }

    /**
     * 获取建设状态
     * @return
     */
    public List<String> getBuildGroup(){
        return tbProjectTrafficMapper.queryBuildGroup();
    }

    /**
     * 获取项目类型
     * @return
     */
    public List<String> getProjectType(){
        return tbProjectTrafficMapper.queryProType();
    }
}
