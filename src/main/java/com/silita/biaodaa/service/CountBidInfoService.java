package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCountBidInfoMapper;
import com.silita.biaodaa.dao.TbPlatformNoticeMapper;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 统计
 */
@Service
public class CountBidInfoService {

    @Autowired
    TbCountBidInfoMapper tbCountBidInfoMapper;
    @Autowired
    TbPlatformNoticeMapper tbPlatformNoticeMapper;

    public Map<String, Object> listCountBid(Map<String, Object> valueMap) {
        Map<String, Object> resultMap = tbPlatformNoticeMapper.queryPlatformInfoByParam(valueMap);
        if (MapUtils.isEmpty(resultMap)){
            return resultMap;
        }
        String releaseTime = MapUtils.getString(resultMap,"releaseTime");
        if(releaseTime.contains("~")){
            String[] str = releaseTime.split("~");
            valueMap.put("statDate",str[0]);
            valueMap.put("endDate",str[1]);
            valueMap.put("count",10);
            resultMap.put("list",  tbCountBidInfoMapper.queryCountBidDateList(valueMap));
            return resultMap;
        }
        resultMap.put("list",tbCountBidInfoMapper.queryCountBidList(valueMap));
        return resultMap;
    }

}
