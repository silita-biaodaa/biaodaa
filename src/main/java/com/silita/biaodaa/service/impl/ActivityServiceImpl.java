package com.silita.biaodaa.service.impl;

import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.TbActivityContentMapper;
import com.silita.biaodaa.dao.TbDuanwuActivityMapper;
import com.silita.biaodaa.dao.UserTempBddMapper;
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
    @Autowired
    TbActivityContentMapper tbActivityContentMapper;
    @Autowired
    UserTempBddMapper userTempBddMapper;

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
        if (tbDuanwuActivityMapper.queryOrderNoExist(activity.getOrderNo()) > 0) {
            tbDuanwuActivityMapper.update(activity);
            return;
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

    @Override
    public boolean isActivity(Map<String, Object> param) {
        Map<String, Object> activityMap = tbActivityContentMapper.queryActivity();
        if (MapUtils.isEmpty(activityMap)) {
            return false;
        }
        String userId = VisitInfoHolder.getUid();
        if (StringUtils.isEmpty(userId)) {
            return false;
        }
        //判断是否参与过活动
        int count = userTempBddMapper.queryRelUserInfo(userId);
        if (count > 0) {
            return false;
        }
        String channel = VisitInfoHolder.getChannel();
        //判断是否APP
        if (("1001".equals(channel) || "1002".equals(channel)) && null != param.get("version")) {
            String version = MapUtils.getString(activityMap, "version");
            String paramVersion = MapUtils.getString(param, "version");
            String[] versions = version.split("\\.");
            String[] paramVersions = paramVersion.split("\\.");
            if (Integer.valueOf(paramVersions[0]) >= Integer.valueOf(versions[0])) {
                return true;
            }
            if (Integer.valueOf(paramVersions[1]) >= Integer.valueOf(versions[1])) {
                return true;
            }
            if (Integer.valueOf(paramVersions[2]) >= Integer.valueOf(versions[2])) {
                return true;
            }
        }
        return true;
    }
}
