package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.TbUserSubscribeMapper;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 订阅Service
 * Created by zhushuai on 2019/9/2.
 */
@Service
public class SubscribeService {

    @Autowired
    TbUserSubscribeMapper tbUserSubscribeMapper;

    /**
     * 设置条件
     *
     * @param param
     * @return
     */
    public void saveSubscribeCondition(String userId, Map<String, Object> param) {
        //将用户的之前的条件设置为不推送
        tbUserSubscribeMapper.updateIsPub(userId);
        if (null == param.get("subType")) {
            param.put("subType", "zhaobiao");
        }
        Map<String, Object> valMap = new HashedMap() {{
            put("userId", userId);
            put("subType", param.get("subType"));
            put("isPush", param.get("isPush"));
        }};
        param.remove("isPush");
        param.remove("subType");
        valMap.put("condition", JSONObject.toJSONString(param));
        tbUserSubscribeMapper.insertUserSubscribe(valMap);
    }

    /**
     * 获取用户最新的订阅条件/根据主键查询条件
     *
     * @param param
     * @return
     */
    public Map<String, Object> getSubscribeCondition(Map<String, Object> param) {
        if (!param.containsKey("pkid") || MyStringUtils.isNull(param.get("pkid"))) {
            param.put("userId", VisitInfoHolder.getUid());
        }
        Map<String, Object> result = tbUserSubscribeMapper.queryNewCondition(param);
        if (MapUtils.isEmpty(result)) {
            return new HashedMap();
        }
        Map<String, Object> conditionMap = JSONObject.parseObject(MapUtils.getString(result, "condition"));
        conditionMap.put("isPush", result.get("isPush"));
        conditionMap.put("subType", result.get("subType"));
        conditionMap.put("pkid", result.get("pkid"));
        return conditionMap;
    }
}
