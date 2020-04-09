package com.silita.biaodaa.service;

import com.silita.biaodaa.model.SysUser;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.silita.biaodaa.common.Constant.PROFIT_S_CODE_REGISTER;

/**
 * Created by zhushuai on 2020/4/9.
 */
@Service
public class GiveUserVipService {

    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    private VipService vipService;

    /**
     * 赠送会员
     *
     * @param param
     */
    @Transactional
    public synchronized boolean giveUserVip(Map<String, Object> param) {
        String phone = MapUtils.getString(param, "phone");
        String pwd = MapUtils.getString(param, "pwd");
        //判断手机号是否存在
        boolean isRegister = authorizeService.isRegisted(phone);
        if (isRegister) {
            return false;
        }
        //注册新用户
        SysUser user = new SysUser();
        user.setChannel("1004");
        user.setLoginPwd(pwd);
        user.setPhoneNo(phone);
        user.setClientVersion("3.0");
        authorizeService.createMemberUser(user);
        //赠送会员
        vipService.addUserProfit(user.getChannel(), user.getPkid(), PROFIT_S_CODE_REGISTER, null);
        return true;
    }

}
