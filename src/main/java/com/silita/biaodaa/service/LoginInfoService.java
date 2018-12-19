package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.TbLoginInfoMapper;
import com.silita.biaodaa.model.TbLoginInfo;
import com.silita.biaodaa.utils.MyDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * tb_login_info Service
 */
@Service
public class LoginInfoService {

    @Autowired
    TbLoginInfoMapper loginInfoMapper;

    /**
     * 保存登录信息
     *
     * @param jsonObject
     */
    public void saveLoginInfo(JSONObject jsonObject) {
        TbLoginInfo loginInfo = new TbLoginInfo();
        loginInfo.setPkid(VisitInfoHolder.getUUID());
        loginInfo.setLoginName(jsonObject.getString("name"));
        loginInfo.setLoginTel(jsonObject.getString("phone"));
        loginInfo.setLoginTime(new Date());
        if (null != jsonObject.get("date")) {
            loginInfo.setLoginTime(MyDateUtils.strToDate(MyDateUtils.longDateToStr(jsonObject.getLong("date"), MyDateUtils.datetimePattern), MyDateUtils.datetimePattern));
        }
        loginInfoMapper.deleteLoginInfo(loginInfo.getLoginTel());
        loginInfoMapper.insertLoginInfo(loginInfo);
    }

}
