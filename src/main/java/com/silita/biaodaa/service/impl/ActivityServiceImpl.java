package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.dao.TbDuanwuActivityMapper;
import com.silita.biaodaa.model.TbDuanwuActivity;
import com.silita.biaodaa.service.ActivityService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * Created by zhushuai on 2019/6/4.
 */
@Service("activityService")
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    TbDuanwuActivityMapper tbDuanwuActivityMapper;

    @Override
    public void saveOrderNo(Map<String, Object> param) {
        String tradeType = MapUtils.getString(param, "tradeType");
        if (StringUtils.isEmpty(tradeType)) {
            tradeType = "ios";
        }
        TbDuanwuActivity activity = new TbDuanwuActivity();
        activity.setOrderNo(MapUtils.getString(param, "orderNo"));
        activity.setInvitedUid(MapUtils.getString(param, "userId"));
        activity.setInvitePhone(MapUtils.getString(param, "phone"));
        activity.setPayState(1);
        activity.setPayAmount(MapUtils.getDouble(param, "amount"));
        activity.setPayd(new Date());
        if ("MWEB".equals(tradeType)) {
            activity.setPayType("wap");
        } else if ("NATIVE".equals(tradeType)) {
            activity.setPayType("web");
        } else if ("APP".equals(tradeType)) {
            activity.setPayType("app");
        } else if ("ios".equals(tradeType)) {
            activity.setPayType("ios");
        }
        tbDuanwuActivityMapper.insert(activity);
    }

    @Override
    public void updatePayState(Map<String, Object> param) {
        TbDuanwuActivity activity = new TbDuanwuActivity();
        activity.setOrderNo(MapUtils.getString(param, "orderNo"));
        activity.setPayState(MapUtils.getInteger(param, "orderStatus"));
        tbDuanwuActivityMapper.updatePayState(activity);
    }
}
