package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.InvitationBddMapper;
import com.silita.biaodaa.dao.MessagePushMapper;
import com.silita.biaodaa.dao.UserTempBddMapper;
import com.silita.biaodaa.model.InvitationBdd;
import com.silita.biaodaa.model.MessagePush;
import com.silita.biaodaa.model.UserTempBdd;
import com.silita.biaodaa.utils.PropertiesUtils;
import com.silita.biaodaa.utils.SignConvertUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by 91567 on 2018/4/12.
 */
@Service("userCenterService")
public class UserCenterService {

    @Autowired
    private InvitationBddMapper invitationBddMapper;
    @Autowired
    private MessagePushMapper messagePushMapper;
    @Autowired
    private UserTempBddMapper userTempBddMapper;

    public UserTempBdd updateUserTemp(UserTempBdd userTempBdd) {
        userTempBddMapper.updateUserTemp(userTempBdd);
        UserTempBdd vo = userTempBddMapper.getUserTempByUserId(userTempBdd.getUserid());
        //权限token
        if (vo != null) {
            Map<String, String> parameters = new HashedMap();
            parameters.put("name", vo.getUsername());
            parameters.put("password", vo.getUserpass());
            parameters.put("phone", vo.getUserphone());
            parameters.put("userId", vo.getUserid());
            try {
                String secret = PropertiesUtils.getProperty("CONTENT_SECRET");
                String sign = SignConvertUtil.generateMD5Sign(secret, parameters);
                String parameterJson = JSONObject.toJSONString(parameters);
                String asB64 = Base64.getEncoder().encodeToString(parameterJson.getBytes("utf-8"));
                String xtoken = sign + "." + asB64;
                vo.setXtoken(xtoken);
            } catch (NoSuchAlgorithmException e) {
            } catch (UnsupportedEncodingException e) {
            }
        }
        return vo;
    }

    public String updatePassWord(UserTempBdd userTempBdd) {
        //判断验证码是否有效
        Map<String, Object> params = new HashMap<>(1);
        params.put("invitationPhone", userTempBdd.getUserphone());
        params.put("invitationCode", userTempBdd.getInvitationCode());
        InvitationBdd invitationVo = invitationBddMapper.getInvitationBddByPhoneAndCode(params);
        if (null == invitationVo) {
            return "验证码错误或无效！";
        } else if("1".equals(invitationVo.getInvitationState())){
            return "验证码失效！";
        }
        //判断前端是否已加密  IOS 密码已加密  Android 密码已加密
        if (userTempBdd.getLoginchannel().equals("1002") && Integer.parseInt(userTempBdd.getVersion()) > 10100) {
        } else if (userTempBdd.getLoginchannel().equals("1001") && Integer.parseInt(userTempBdd.getVersion()) > 10600) {
        } else {
            userTempBdd.setUserpass(DigestUtils.shaHex(userTempBdd.getUserpass()));
        }
        userTempBddMapper.updatePassWdByUserIdAndPhone(userTempBdd);
        //更新验证码状态
        invitationBddMapper.updateInvitationBddByCodeAndPhone(params);
        return "";
    }

    public PageInfo queryMessageList(Page page, Map params){
        List<MessagePush> messagePushes = new ArrayList<>();

        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        messagePushes = messagePushMapper.listMessageByUserIdAndType(params);
        PageInfo pageInfo = new PageInfo(messagePushes);
        return pageInfo;
    }

}
