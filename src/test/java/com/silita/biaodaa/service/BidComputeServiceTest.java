package com.silita.biaodaa.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangxiahui on 18/7/3.
 */
public class BidComputeServiceTest extends ConfigTest {

    @Autowired
    BidComputeService bidComputeService;

    @Test
    public void computeHandler(){

        // TODO: 18/7/3 获取模板数据
        List<Map<String, Object>> conditionBeans = new ArrayList<>();
        Map<String,Object> projectMap = new HashMap<>();
        projectMap.put("code","Project");
        projectMap.put("resourceName","类似项目业绩");
        projectMap.put("overflow",5);
        projectMap.put("provinceInner",5);
        projectMap.put("provinceOuter",3);
        conditionBeans.add(projectMap);
        try {
            Map<String,Object> result = bidComputeService.computeHandler(new HashMap<>(),conditionBeans);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
