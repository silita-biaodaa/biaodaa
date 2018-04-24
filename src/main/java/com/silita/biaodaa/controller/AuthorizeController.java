package com.silita.biaodaa.controller;

import com.silita.biaodaa.model.InvitationBdd;
import com.silita.biaodaa.model.UserTempBdd;
import com.silita.biaodaa.service.AuthorizeService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户授权 如注册、登录、授权
 * Created by 91567 on 2018/4/13.
 */
@RequestMapping("/authorize")
@Controller
public class AuthorizeController {
    private static final Logger logger = Logger.getLogger(CompanyController.class);

    @Autowired
    private AuthorizeService authorizeService;


    @ResponseBody
    @RequestMapping(value = "/userRegister",produces = "application/json;charset=utf-8")
    public Map<String,Object> userRegister(@RequestBody UserTempBdd userTempBdd){
        Map result = new HashMap();
        result.put("code", 1);
        result.put("data", null);

        try{
            String msg = authorizeService.addUserTemp(userTempBdd);
            if("".equals(msg)) {
                result.put("msg", "用户注册成功！");
                result.put("data", authorizeService.queryUserTempByUserPhone(userTempBdd));
            } else {
                result.put("code", 0);
                result.put("msg", msg);
            }
        } catch (Exception e) {
            logger.error("用户注册异常！" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/userLogin",produces = "application/json;charset=utf-8")
    public Map<String,Object> userLogin(@RequestBody UserTempBdd userTempBdd){
        Map result = new HashMap();
        result.put("code", 1);
        result.put("data", null);

        try{
            UserTempBdd vo = authorizeService.queryUserTempByUserNameOrPhoneAndPassWd(userTempBdd);
            if(vo != null) {
                result.put("msg", "用户登录成功！");
                result.put("data", vo);
            } else {
                result.put("code", 0);
                result.put("msg", "用户名或密码错误!");
            }
        } catch (Exception e) {
            logger.error("用户登录异常！" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/thirdPartyBindingOrRegister",produces = "application/json;charset=utf-8")
    public Map<String,Object> thirdPartyBinding(@RequestBody UserTempBdd userTempBdd){
        Map result = new HashMap();
        result.put("code", 1);
        result.put("data", null);

        try{
            String msg = authorizeService.updateOrInsetUserTemp(userTempBdd);
            if("".equals(msg)) {
                if(userTempBdd.getType() == 1) {
                    result.put("msg", "绑定微信成功！");
                } else if(userTempBdd.getType() == 2) {
                    result.put("msg", "绑定QQ成功！");
                }
                result.put("data", authorizeService.queryUserTempByUserPhone(userTempBdd));
            } else {
                result.put("code", 0);
                result.put("msg", msg);
            }
        } catch (Exception e) {
            logger.error("用户绑定第三方账号异常！" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/thirdPartyLogin",produces = "application/json;charset=utf-8")
    public Map<String,Object> thirdPartyLogin(@RequestBody UserTempBdd userTempBdd){
        Map result = new HashMap();
        result.put("code", 1);
        result.put("data", null);

        try{
            UserTempBdd vo = authorizeService.queryUserTempByWXUnionIdOrQQOpenId(userTempBdd);
            if(vo != null) {
                result.put("msg", "第三方登录成功！");
                result.put("data", vo);
            } else {
                result.put("code", 2);
                result.put("msg", "第三方账号未绑定!");
            }
        } catch (Exception e) {
            logger.error("第三方登录异常！" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/forgotPassword",produces = "application/json;charset=utf-8")
    public Map<String,Object> forgotPassword(@RequestBody UserTempBdd userTempBdd){
        Map result = new HashMap();
        result.put("code", 1);

        try{
            String msg = authorizeService.UpdatePassWdByForgotPassword(userTempBdd);
            if("".equals(msg)) {
                result.put("msg", "找回密码成功！");
            } else {
                result.put("code", 0);
                result.put("msg", msg);
            }
        } catch (Exception e) {
            logger.error("找回密码异常！" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/weChatLogin",produces = "application/json;charset=utf-8")
    public Map<String,Object> weChatLogin(@RequestBody String code){
        Map result = new HashMap();
        result.put("code", 0);
        result.put("data", null);
        try{
            Map map = authorizeService.weChatLogin(code);
            int codeTemp = (int)map.get("code");
            if(codeTemp == 3) {
                result.put("code", 3);
                map.put("msg", "用户已取消登录！");
            } else if(codeTemp == 1) {
                result.put("code", 1);
                map.put("msg", "微信扫码登录成功！");
                result.put("data", map.get("data"));
            } else if(codeTemp == 2) {
                result.put("code", 2);
                map.put("msg", "用户还未绑定！");
            }
        } catch (Exception e) {
            logger.error("微信扫描登录异常！" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/getVerificationCode",produces = "application/json;charset=utf-8")
    public Map<String,Object> getVerificationCode(@RequestBody InvitationBdd invitation, HttpServletRequest request){
        String adder = request.getHeader("X-real-ip");
        Map result = new HashMap();
        result.put("code", 1);

        try{
            invitation.setInvitationIp(adder);
            String msg = authorizeService.sendRegisterVerificationCode(invitation);
            if("".equals(msg)) {
                result.put("msg", "获取验证码信息成功！");
            } else {
                result.put("code", 0);
                result.put("msg", msg);
            }
        } catch (Exception e) {
            logger.error("获取验证码信息异常！" + e.getMessage(), e);
            result.put("code",0);
            result.put("msg",e.getMessage());
        }
        return result;
    }
}
