package com.silita.biaodaa.service;

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
     */
    public void saveLoginInfo(String name,String phone,Long date) {
        TbLoginInfo loginInfo = new TbLoginInfo();
        loginInfo.setPkid(VisitInfoHolder.getUUID());
        loginInfo.setLoginName(name);
        loginInfo.setLoginTel(phone);
        loginInfo.setLoginTime(new Date());
        if (null != date) {
            loginInfo.setLoginTime(MyDateUtils.strToDate(MyDateUtils.longDateToStr(date, MyDateUtils.datetimePattern), MyDateUtils.datetimePattern));
        }
        if(loginInfoMapper.queryCount(loginInfo.getLoginTel()) > 0){
            return;
        }
        loginInfoMapper.insertLoginInfo(loginInfo);
    }

}
