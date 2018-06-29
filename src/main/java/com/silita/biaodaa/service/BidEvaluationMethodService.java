package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.SnatchurlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 评标办法service
 */
@Service
public class BidEvaluationMethodService {

    @Autowired
    SnatchurlMapper snatchurlMapper;

    /**
     * 获取项目名称
     * @param param
     * @return
     */
    public List<Map<String,Object>> getSnatchUrlList(Map<String,Object> param){
        if(null == param.get("limit")){
            param.put("limit",5);
        }
        return snatchurlMapper.querySnatchurlList(param);
    }

    public List<Map<String,Object>> getNoticeDetailList(Map<String,Object> param){
        return snatchurlMapper.queryNoticeDetailBySnatchurlId(param);
    }
}
