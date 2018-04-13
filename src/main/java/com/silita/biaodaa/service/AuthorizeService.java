package com.silita.biaodaa.service;

import com.silita.biaodaa.common.SendMessage;
import com.silita.biaodaa.dao.InvitationBddMapper;
import com.silita.biaodaa.dao.UserRoleBddMapper;
import com.silita.biaodaa.dao.UserTempBddMapper;
import com.silita.biaodaa.model.InvitationBdd;
import com.silita.biaodaa.model.UserTempBdd;
import com.silita.biaodaa.utils.CommonUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 权限模块 如注册、登录、授权
 * Created by 91567 on 2018/4/13.
 */
@Service("authorizeService")
public class AuthorizeService {

    @Autowired
    private InvitationBddMapper invitationBddMapper;
    @Autowired
    private UserTempBddMapper userTempBddMapper;
    @Autowired
    private UserRoleBddMapper userRoleBddMapper;

    /**
     * 用户注册
     * @param userTempBdd
     * @return
     */
    public String addUserTemp(UserTempBdd userTempBdd){
        //判断是否已注册
        UserTempBdd userVo = userTempBddMapper.getUserByUserPhone(userTempBdd.getUserphone());
        if(userVo != null && !StringUtils.isEmpty(userVo.getUserphone())) {
            return "手机号码已被注册！";
        }
        //判断验证码是否有效
        Map<String, Object> params = new HashMap<>(1);
        params.put("invitationPhone", userTempBdd.getUserphone());
        params.put("invitationCode", userTempBdd.getInvitationCode());
        InvitationBdd invitationVo = invitationBddMapper.getInvitationBddByPhoneAndCode(params);
        if(invitationVo != null &&  !"0".equals(invitationVo.getInvitationState())) {
            return "验证码错误或无效！";
        }
        //判断前端是否已加密  IOS 密码已加密  Android 密码已加密
        if(userTempBdd.getLoginchannel().equals("1002") && Integer.parseInt(userTempBdd.getVersion()) > 10100) {
        }else if(userTempBdd.getLoginchannel().equals("1001") && Integer.parseInt(userTempBdd.getVersion()) > 10600) {
        } else {
            userTempBdd.setUserpass(DigestUtils.shaHex(userTempBdd.getUserpass()));
        }
        userTempBdd.setUserid(CommonUtil.getUUID());
        userTempBddMapper.InsertUserTemp(userTempBdd);
        //更新验证码状态
        invitationBddMapper.updateInvitationBddByCode(userTempBdd.getInvitationCode());
        //添加角色
        userRoleBddMapper.insertuserRole(userTempBdd.getUserid());
        return "";
    }

    /**
     * 用户登录
     * @param userTempBdd
     * @return
     */
    public UserTempBdd queryUserTempByUserNameOrPhoneAndPassWd(UserTempBdd userTempBdd) {
        //判断前端是否已加密  IOS 密码已加密  Android 密码已加密
        if(userTempBdd.getLoginchannel().equals("1002") && Integer.parseInt(userTempBdd.getVersion()) > 10100) {
        }else if(userTempBdd.getLoginchannel().equals("1001") && Integer.parseInt(userTempBdd.getVersion()) > 10600) {
        } else {
            userTempBdd.setUserpass(DigestUtils.shaHex(userTempBdd.getUserpass()));
        }
        return userTempBddMapper.getUserByUserNameOrPhoneAndPassWd(userTempBdd);
    }

    /**
     * 注册验证码
     *
     * @param invitationBdd
     */
    public String sendRegisterVerificationCode(InvitationBdd invitationBdd) {
//        Integer currentNum = invitationBddMapper.getTotalByPhoneAndCreateDate(invitationBdd);
//        if(currentNum > 10) {
//            return "该手机单天发生的验证码多余10次！";
//        } else {
        //随机生成验证码
        String code = CommonUtil.verificationCode();
        invitationBdd.setInvitationCode(code);
        //发送信息
        String flag = SendMessage.registerCode(code, invitationBdd.getInvitationPhone());
        //发生成功
        if ("0".equals(flag)) {
            invitationBddMapper.insertInvitationBdd(invitationBdd);
            return "";
        } else {
            return flag;
        }
//        }
    }

    /**
     * 找回密码验证码
     *
     * @param invitationBdd
     */
    public String sendUpdatePassWdVerificationCode(InvitationBdd invitationBdd) {
        //随机生成验证码
        String code = CommonUtil.verificationCode();
        invitationBdd.setInvitationCode(code);
        //发送信息
        String flag = SendMessage.updatePasswdCode(code, invitationBdd.getInvitationPhone());
        //发生成功
        if ("0".equals(flag)) {
            invitationBddMapper.insertInvitationBdd(invitationBdd);
            return "";
        } else {
            return flag;
        }
    }

    /**
     *
     * @param userTempBdd
     * @return
     */
    public UserTempBdd queryUserTempByUserPhone(UserTempBdd userTempBdd) {
        return userTempBddMapper.getUserByUserPhone(userTempBdd.getUserphone());
    }
}
