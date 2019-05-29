package com.silita.biaodaa.controller;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.model.InvitationBdd;
import com.silita.biaodaa.model.SysUser;
import com.silita.biaodaa.model.UserTempBdd;
import com.silita.biaodaa.service.AuthorizeService;
import com.silita.biaodaa.service.VipService;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.silita.biaodaa.common.Constant.PROFIT_S_CODE_FIRST;
import static com.silita.biaodaa.common.Constant.PROFIT_S_CODE_INVITE;

/**
 * 用户授权 如注册、登录、授权
 * Created by 91567 on 2018/4/13.
 */
@RequestMapping("/authorize")
@Controller
public class AuthorizeController extends BaseController {
    private static final Logger logger = Logger.getLogger(CompanyController.class);

    @Autowired
    private AuthorizeService authorizeService;

    @Autowired
    VipService vipService;

    /**
     * 注册
     *
     * @param userTempBdd
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userRegister", produces = "application/json;charset=utf-8")
    public Map<String, Object> userRegister(@RequestBody UserTempBdd userTempBdd) {
        Map result = new HashMap();
//        result.put("code", 1);
//        result.put("data", null);
//
//        try {
//            String msg = authorizeService.addUserTemp(userTempBdd);
//            if ("".equals(msg)) {
//                result.put("msg", "用户注册成功！");
//                //用户新表set
//                SysUser user = this.setUser(userTempBdd);
////                result.put("data", authorizeService.queryUserTempByUserPhone(userTempBdd));
//                return this.memberRegister(user);
//            } else {
//                result.put("code", 0);
//                result.put("msg", msg);
//                return result;
//            }
//        } catch (Exception e) {
//            logger.error("用户注册异常！" + e.getMessage(), e);
//            result.put("code", 0);
//            result.put("msg", e.getMessage());
//        }
        result.put("code", 0);
        result.put("msg", "注册用户失败，请更新app版本再注册。");
        return result;
    }

    private String verifyRegisterInfo(SysUser sysUser) {
        if (MyStringUtils.isNull(sysUser.getLoginName()) && MyStringUtils.isNull(sysUser.getPhoneNo())) {
            return "登录名或手机号不能为空！";
        } else if (MyStringUtils.isNull(sysUser.getLoginPwd())) {
            return "登录密码不能为空！";
        } else if (MyStringUtils.isNull(sysUser.getChannel())) {
            return "渠道号不能为空！";
        } else if (MyStringUtils.isNull(sysUser.getClientVersion())) {
            return "终端版本号不能为空！";
        }
        return null;
    }

    /**
     * 邀请会员权益赠送
     *
     * @param sysUser
     */
    private void inviteProfitDeliver(SysUser sysUser) {
        if (MyStringUtils.isNotNull(sysUser.getInviterCode())) {
            String errMsg = vipService.addUserProfit(sysUser.getChannel(), null, PROFIT_S_CODE_INVITE, sysUser.getInviterCode());
            if (errMsg != null) {
                logger.error("会员权益赠送出错[userId:" + sysUser.getPkid() + "][sCode:" + PROFIT_S_CODE_INVITE + "]：" + errMsg);
            }
        }
    }

    private void firstProfitDeliver(SysUser sysUser) {
        Integer hisCount = vipService.queryUserProfitCount(PROFIT_S_CODE_FIRST, sysUser.getPkid());
        if (hisCount == 0) {
            String errMsg = vipService.addUserProfit(sysUser.getChannel(), sysUser.getPkid(), PROFIT_S_CODE_FIRST);
            if (errMsg != null) {
                logger.error("会员权益赠送出错[userId:" + sysUser.getPkid() + "][sCode:" + PROFIT_S_CODE_FIRST + "]：" + errMsg);
            }
        } else {
            logger.debug("权益赠送取消，已存在权益收益！");
        }
    }

    /**
     * 会员模块_用户登录
     *
     * @param sysUser
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/memberLogin", produces = "application/json;charset=utf-8")
    public Map<String, Object> memberLogin(@RequestBody SysUser sysUser) {
        Map result = new HashMap();
        result.put("data", null);
        if (MyStringUtils.isNull(sysUser.getLoginName()) && MyStringUtils.isNull(sysUser.getPhoneNo())) {
            result.put("code", Constant.FAIL_CODE);
            result.put("msg", "登录用户名/手机号不能为空！");
            return result;
        } else if (MyStringUtils.isNull(sysUser.getLoginPwd())) {
            result.put("code", Constant.FAIL_CODE);
            result.put("msg", "登录密码不能为空！");
            return result;
        }

        try {
            SysUser userInfo = authorizeService.memberLogin(sysUser);
            if (userInfo != null) {
                //会员权益赠送
                firstProfitDeliver(userInfo);
                result.put("code", Constant.SUCCESS_CODE);
                result.put("msg", "用户登录成功！");
                result.put("data", userInfo);
            } else {
                result.put("code", Constant.FAIL_CODE);
                result.put("msg", "登录用户或密码错误!");
            }
        } catch (Exception e) {
            logger.error("用户登录异常！" + e.getMessage(), e);
            result.put("code", Constant.FAIL_CODE);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/memberRegister", produces = "application/json;charset=utf-8")
    public Map<String, Object> memberRegister(@RequestBody SysUser sysUser) {
        Map result = new HashMap();
        try {
            String errMsg = verifyRegisterInfo(sysUser);
            if (errMsg != null) {
                result.put("code", Constant.ERR_VIEW_CODE);
                result.put("msg", errMsg);
            } else {
                bulidUserPwd(sysUser);
                String msgCode = authorizeService.registerUser(sysUser);
                if (msgCode.equals(Constant.SUCCESS_CODE)) {
                    SysUser vo = authorizeService.memberLogin(sysUser);
                    vo.setUserphone(vo.getPhoneNo());
                    //会员权益赠送
                    inviteProfitDeliver(sysUser);
                    firstProfitDeliver(vo);
                    result.put("code", Constant.SUCCESS_CODE);
                    result.put("data", vo);
                    result.put("msg", "用户注册成功！");
                } else {
                    result.put("code", msgCode);
                    String msg = null;
                    if (msgCode.equals(Constant.ERR_VERIFY_PHONE_CODE)) {
                        msg = "验证码失效或错误！";
                    } else if (msgCode.equals(Constant.ERR_USER_EXIST)) {
                        msg = "用户名或手机号已存在！";
                    } else if (msgCode.equals(Constant.ERR_VERIFY_IVITE_CODE)) {
                        msg = "推荐人邀请码错误！";
                    } else {
                        msg = "未知错误码！";
                    }
                    result.put("msg", msg);
                }
            }
        } catch (Exception e) {
            String eMsg = "用户注册异常！" + e.getMessage();
            logger.error(eMsg, e);
            result.put("code", Constant.EXCEPTION_CODE);
            result.put("msg", eMsg);
        }
        return result;
    }

    /**
     * 用户登录
     *
     * @param userTempBdd
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/userLogin", produces = "application/json;charset=utf-8")
    public Map<String, Object> userLogin(@RequestBody UserTempBdd userTempBdd) {
        Map result = new HashMap();
        result.put("code", 1);
        result.put("data", null);

        try {
            String msg = authorizeService.checkUserPhone(userTempBdd);
            if ("".equals(msg)) {
                UserTempBdd vo = authorizeService.queryUserTempByUserNameOrPhoneAndPassWd(userTempBdd);
                if (vo != null) {
                    result.put("msg", "用户登录成功！");
                    result.put("data", vo);
                } else {
                    result.put("code", 0);
                    result.put("msg", "手机号码或密码错误!");
                }
            } else {
                result.put("code", 0);
                result.put("msg", "手机号码不存在!");
            }
        } catch (Exception e) {
            logger.error("用户登录异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    private String preThirdBinding(SysUser sysUser) {
        if (MyStringUtils.isNull(sysUser.getQqOpenId()) &&
                MyStringUtils.isNull(sysUser.getWxOpenId()) && MyStringUtils.isNull(sysUser.getWxUnionId())) {
            return Constant.ERR_VIEW_CODE;
        }
//        if(MyStringUtils.isNull(sysUser.getLoginPwd())){
//            return Constant.ERR_VIEW_CODE;
//        }
        if (MyStringUtils.isNull(sysUser.getPhoneNo())) {
            return Constant.ERR_VIEW_CODE;
        }
        if (MyStringUtils.isNull(sysUser.getChannel())) {
            return Constant.ERR_VIEW_CODE;
        }
        if (MyStringUtils.isNull(sysUser.getVerifyCode())) {
            return Constant.ERR_VERIFY_PHONE_CODE;
        }

        String code = authorizeService.verifyPhoneCode(sysUser);
        if (code != null) {
            return code;
        }
        code = authorizeService.verifyInviterCode(sysUser);
        if (code != null) {
            return code;
        }
        return null;
    }

    private String preThirdLogin(SysUser sysUser) {
        if (MyStringUtils.isNull(sysUser.getQqOpenId()) &&
                MyStringUtils.isNull(sysUser.getWxOpenId()) && MyStringUtils.isNull(sysUser.getWxUnionId())) {
            return Constant.ERR_VIEW_CODE;
        }
        if (MyStringUtils.isNull(sysUser.getChannel())) {
            return Constant.ERR_NULL_CHANNEL;
        }
        return null;
    }

    /**
     * 第三方登录
     *
     * @param sysUser
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/memberThirdLogin", produces = "application/json;charset=utf-8")
    public Map<String, Object> memberThirdLogin(@RequestBody SysUser sysUser) {
        Map result = new HashMap();
        try {
            String errCode = preThirdLogin(sysUser);
            if (errCode == null) {
                SysUser vo = authorizeService.memberThirdLogin(sysUser);
                if (vo != null) {
                    //会员权益赠送
                    firstProfitDeliver(vo);
                    result.put("code", Constant.SUCCESS_CODE);
                    result.put("msg", "第三方登录成功！");
                    result.put("data", vo);
                } else {
                    result.put("code", Constant.THIRD_USER_NOTEXIST);
                    result.put("msg", "第三方用户不存在");
                }
            } else {
                result.put("code", errCode);
                result.put("msg", "第三方登录失败！");
            }
        } catch (Exception e) {
            logger.error("第三方登录异常！" + e.getMessage(), e);
            result.put("code", Constant.EXCEPTION_CODE);
            result.put("msg", "第三方登录异常." + e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/thirdPartyBinding", produces = "application/json;charset=utf-8")
    public Map<String, Object> thirdPartyBinding(@RequestBody SysUser sysUser) {
        Map result = new HashMap();
        try {
            String errCode = preThirdBinding(sysUser);
            if (errCode == null) {
                bulidUserPwd(sysUser);
                Integer count = authorizeService.thirdPartyBinding(sysUser);
                if (count != null) {
                    SysUser vo = authorizeService.memberLogin(sysUser);
                    vo.setUserphone(vo.getPhoneNo());
                    vo.setImgurl(vo.getImageUrl());
                    vo.setNickname(vo.getNikeName());
                    if (count == 22) {
                        //会员权益赠送
                        inviteProfitDeliver(sysUser);
                    }
                    firstProfitDeliver(vo);
                    result.put("code", Constant.SUCCESS_CODE);
                    result.put("msg", "绑定成功！");
                    result.put("data", vo);
                } else {
                    result.put("code", Constant.FAIL_CODE);
                    result.put("msg", "绑定失败，请重试。");
                }
            } else {
                result.put("code", errCode);
                result.put("msg", "绑定失败！");
            }
        } catch (Exception e) {
            logger.error("第三方绑定异常！" + e.getMessage(), e);
            result.put("code", Constant.EXCEPTION_CODE);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 第三方绑定或注册
     *
     * @param userTempBdd
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/thirdPartyBindingOrRegister", produces = "application/json;charset=utf-8")
    public Map<String, Object> thirdPartyBinding(@RequestBody UserTempBdd userTempBdd) {
        Map result = new HashMap();
//        result.put("code", 1);
//        result.put("data", null);
//
//        try {
//            String msg = authorizeService.updateOrInsetUserTemp(userTempBdd);
//            if ("".equals(msg)) {
//                if (userTempBdd.getType() == 1) {
//                    result.put("msg", "绑定微信成功！");
//                } else if (userTempBdd.getType() == 2) {
//                    result.put("msg", "绑定QQ成功！");
//                }
//                SysUser user = this.setUser(userTempBdd);
//                return thirdPartyBinding(user);
//            } else {
//                result.put("code", 0);
//                result.put("msg", msg);
//                return result;
//            }
//        } catch (Exception e) {
//            logger.error("用户绑定第三方账号异常！" + e.getMessage(), e);
//            result.put("code", 0);
//            result.put("msg", e.getMessage());
//        }
        result.put("code", 0);
        result.put("msg", "第三方绑定失败，请更新app版本再进行操作。");
        return result;
    }

    /**
     * 第三方登录
     *
     * @param userTempBdd
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/thirdPartyLogin", produces = "application/json;charset=utf-8")
    public Map<String, Object> thirdPartyLogin(@RequestBody UserTempBdd userTempBdd) {
        Map result = new HashMap();
        result.put("code", 1);
        result.put("data", null);

        try {
            UserTempBdd vo = authorizeService.queryUserTempByWXUnionIdOrQQOpenId(userTempBdd);
            if (vo != null) {
                result.put("msg", "第三方登录成功！");
                result.put("data", vo);
            } else {
                result.put("code", 2);
                result.put("msg", "第三方账号未绑定!");
            }
        } catch (Exception e) {
            logger.error("第三方登录异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 忘记密码
     *
     * @param userTempBdd
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/forgotPassword", produces = "application/json;charset=utf-8")
    public Map<String, Object> forgotPassword(@RequestBody UserTempBdd userTempBdd) {
        Map result = new HashMap();
        result.put("code", 1);

        try {
            String msg = authorizeService.UpdatePassWdByForgetPassword(userTempBdd);
            if ("".equals(msg)) {
                result.put("msg", "找回密码成功！");
            } else {
                result.put("code", 0);
                result.put("msg", msg);
            }
        } catch (Exception e) {
            logger.error("找回密码异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 微信登录
     *
     * @param code
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/weChatLogin", produces = "application/json;charset=utf-8")
    public Map<String, Object> weChatLogin(@RequestBody String code) {
        Map result = new HashMap();
        result.put("code", 0);
        result.put("data", null);
        try {
            Map map = authorizeService.weChatLogin(code);
            int codeTemp = (int) map.get("code");
            if (codeTemp == 3) {
                result.put("code", 3);
                map.put("msg", "用户已取消登录！");
            } else if (codeTemp == 1) {
                result.put("code", 1);
                map.put("msg", "微信扫码登录成功！");
                result.put("data", map.get("data"));
            } else if (codeTemp == 2) {
                result.put("code", 2);
                map.put("msg", "用户还未绑定！");
            }
        } catch (Exception e) {
            logger.error("微信扫描登录异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    /**
     * 获取验证码
     *
     * @param invitation
     * @param request
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getVerificationCode", produces = "application/json;charset=utf-8")
    public Map<String, Object> getVerificationCode(@RequestBody InvitationBdd invitation, HttpServletRequest request) {
        String adder = request.getHeader("X-real-ip");
        Map result = new HashMap();
        result.put("code", 1);

        try {
            invitation.setInvitationIp(adder);
            String msg = authorizeService.sendRegisterVerificationCode(invitation);
            if ("".equals(msg)) {
                result.put("msg", "获取验证码信息成功！");
            } else {
                result.put("code", 0);
                result.put("msg", msg);
            }
        } catch (Exception e) {
            logger.error("获取验证码信息异常！" + e.getMessage(), e);
            result.put("code", 0);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    private String preVerifyCode(InvitationBdd invitation) {
        if (MyStringUtils.isNull(invitation.getInvitationPhone())) {
            return "手机号不能为空";
        }
        if (MyStringUtils.isNull(invitation.getType())) {
            return "type不能为空";
        }
        return null;
    }

    @ResponseBody
    @RequestMapping(value = "/getVerifyCode", produces = "application/json;charset=utf-8")
    public Map<String, Object> getVerifyCode(@RequestBody InvitationBdd invitation, HttpServletRequest request) {
        String adder = request.getHeader("X-real-ip");
        Map result = new HashMap();
        try {
            String preMsg = preVerifyCode(invitation);
            if (preMsg != null) {
                result.put("code", Constant.ERR_VIEW_CODE);
                result.put("msg", preMsg);
                return result;
            }

            Integer type = invitation.getType();
            invitation.setInvitationIp(adder);
            boolean isSendMsg = true;
            if (type == 1) {//注册
                invitation.setMsgTemplate("SMS_104720006");
                boolean registed = authorizeService.isRegisted(invitation.getInvitationPhone());
                if (registed) {
                    isSendMsg = false;
                    result.put("code", Constant.HINT_IS_REGIST);
                    result.put("msg", "手机号已注册,请登录");
                }
            } else if (type == 2) {//找回密码和修改密码
                invitation.setMsgTemplate("SMS_104665007");
                boolean registed = authorizeService.isRegisted(invitation.getInvitationPhone());
                if (!registed) {
                    isSendMsg = false;
                    result.put("code", Constant.HINT_NOT_REGIST);
                    result.put("msg", "手机号还未注册");
                }
            } else if (type == 3) {//绑定
                invitation.setMsgTemplate("SMS_104720006");
                String errCode = authorizeService.hasInviterCode(invitation.getInvitationPhone());
                if (errCode.equals(Constant.ERR_EXISTS_IVITE_CODE)) {
                    result.put("msg", "推荐人邀请码已存在");
                } else if (errCode.equals(Constant.HINT_IS_REGIST)) {
                    result.put("msg", "用户已注册");
                } else if (errCode.equals(Constant.HINT_NOT_REGIST)) {
                    result.put("msg", "此用户未注册");
                } else {
                    result.put("msg", "用户信息匹配异常");
                }
                result.put("code", errCode);
            }

            if (isSendMsg) {
                String errMsg = authorizeService.sendShorMsgVerfiyCode(invitation);
                if (errMsg == null) {
                    if (MyStringUtils.isNull(result.get("code"))) {
                        result.put("code", Constant.SUCCESS_CODE);
                    }
                    if (MyStringUtils.isNull(result.get("msg"))) {
                        result.put("msg", "获取验证码信息成功！");
                    }
                } else {
                    result.put("code", Constant.FAIL_CODE);
                    result.put("msg", errMsg);
                }
            }
        } catch (Exception e) {
            logger.error("获取验证码信息异常！" + e.getMessage(), e);
            result.put("code", Constant.EXCEPTION_CODE);
            result.put("msg", e.getMessage());
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/address", produces = "application/json;charset=utf-8")
    public Map<String, Object> getIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.length() > 2) {
            ip = ip.split(",")[0];
        }
        Map result = new HashMap();
        try {
            logger.info("----------------ip:" + ip + "-------------------------");
            successMsg(result, authorizeService.getIpAddress(ip));
            return result;
        } catch (Exception e) {
            logger.error("获取ip失败", e);
        }
        return result;
    }

    @ResponseBody
    @RequestMapping(value = "/openid", produces = "application/json;charset=utf-8")
    public Map<String, Object> getOpenid(@RequestBody Map<String, Object> param) {
        Map result = new HashMap();
        String code = MapUtils.getString(param, "code");
        successMsg(result, authorizeService.getOppenid(code));
        return result;
    }
}
