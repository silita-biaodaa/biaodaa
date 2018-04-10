package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.CommonMapper;
import com.silita.biaodaa.utils.DateUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/4/10.
 */
@Service
public class CommonService {
    @Autowired
    private CommonMapper commonMapper;

    public List<Map<String, Object>> queryPbMode(){
        return commonMapper.queryPbMode();
    }

    public List queryCertZzByCompanyName(String companyName){
        return commonMapper.queryCertZzByCompanyName(companyName);
    }

    //记录用户访问模块轨迹
    public void insertRecordLog(String type, String userId, String ipAddres) {
        Map argMap =new HashedMap();
        argMap.put("type",type);
        argMap.put("userId",userId);
        argMap.put("type",type);
        argMap.put("ip",ipAddres);
        argMap.put("date",DateUtils.getDate(null));
        int clickCount = commonMapper.queryClickByUserId(argMap);
        if (clickCount > 0) { // 存在点击则修改点击次数和最后一次点击时间
            commonMapper.updateUserClick(argMap);
        } else { // 不存在则新增一条
            commonMapper.insertUserClick(argMap);
        }
    }

}
