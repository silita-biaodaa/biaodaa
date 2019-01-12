package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.SendMessage;
import com.silita.biaodaa.common.WeChat.model.WXAccessToken;
import com.silita.biaodaa.common.WeChat.model.WXUserInfo;
import com.silita.biaodaa.common.WeChat.util.WeChatLoginUtil;
import com.silita.biaodaa.dao.InvitationBddMapper;
import com.silita.biaodaa.dao.UserRoleBddMapper;
import com.silita.biaodaa.dao.UserTempBddMapper;
import com.silita.biaodaa.model.InvitationBdd;
import com.silita.biaodaa.model.SysUser;
import com.silita.biaodaa.model.SysUserRole;
import com.silita.biaodaa.model.UserTempBdd;
import com.silita.biaodaa.utils.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Base64.getEncoder;

/**
 * 权限模块 如注册、登录、授权
 * Created by 91567 on 2018/4/13.
 */
@Transactional
@Service("authorizeService")
public class AuthorizeService {
    private Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private InvitationBddMapper invitationBddMapper;
    @Autowired
    private UserTempBddMapper userTempBddMapper;
    @Autowired
    private UserRoleBddMapper userRoleBddMapper;

    @Autowired
    private VipService vipService;
    /**
     * 用户注册
     *
     * @param userTempBdd
     * @return
     */
    public String addUserTemp(UserTempBdd userTempBdd) {
        //判断是否已注册
        UserTempBdd userVo = userTempBddMapper.getUserByUserPhone(userTempBdd.getUserphone());
        if (userVo != null && !StringUtils.isEmpty(userVo.getUserphone())) {
            return "手机号码已被注册！";
        }
        //判断验证码是否有效
        Map<String, Object> params = new HashMap<>(1);
        params.put("invitationPhone", userTempBdd.getUserphone());
        params.put("invitationCode", userTempBdd.getInvitationCode());
        InvitationBdd invitationVo = invitationBddMapper.getInvitationBddByPhoneAndCode(params);
        if (null == invitationVo) {
            return "验证码错误或无效！";
        } else if ("1".equals(invitationVo.getInvitationState())) {
            return "验证码失效！";
        }
        //判断前端是否已加密  IOS 密码已加密  Android 密码已加密
        if (userTempBdd.getLoginchannel().equals("1002") && Integer.parseInt(userTempBdd.getVersion()) > 10100) {
        } else if (userTempBdd.getLoginchannel().equals("1001") && Integer.parseInt(userTempBdd.getVersion()) > 10600) {
        } else {
            userTempBdd.setUserpass(DigestUtils.shaHex(userTempBdd.getUserpass()));
        }
        userTempBdd.setUserid(CommonUtil.getUUID());
        //添加用户及角色
        userTempBddMapper.InsertUserTemp(userTempBdd);
        userRoleBddMapper.insertUserRole(userTempBdd.getUserid());
        //更新验证码状态
        invitationBddMapper.updateInvitationBddByCodeAndPhone(params);
        return "";
    }

    /**
     * 第三方绑定 微信\qq
     * 1微信 2QQ
     *
     * @param userTempBdd
     * @return
     */
    public String updateOrInsetUserTemp(UserTempBdd userTempBdd) {
        //判断验证码是否有效
        Map<String, Object> params = new HashMap<>(1);
        params.put("invitationPhone", userTempBdd.getUserphone());
        params.put("invitationCode", userTempBdd.getInvitationCode());
        InvitationBdd invitationVo = invitationBddMapper.getInvitationBddByPhoneAndCode(params);
        if (null == invitationVo) {
            return "验证码错误或无效！";
        } else if ("1".equals(invitationVo.getInvitationState())) {
            return "验证码失效！";
        }
        //判断前端是否已加密  IOS 密码已加密  Android 密码已加密
        if (userTempBdd.getLoginchannel().equals("1002") && Integer.parseInt(userTempBdd.getVersion()) > 10100) {
        } else if (userTempBdd.getLoginchannel().equals("1001") && Integer.parseInt(userTempBdd.getVersion()) > 10600) {
        } else {
            userTempBdd.setUserpass(DigestUtils.shaHex(userTempBdd.getUserpass()));
        }
        UserTempBdd vo = userTempBddMapper.getUserByUserPhone(userTempBdd.getUserphone());
        if (vo == null) {
            userTempBdd.setUserid(CommonUtil.getUUID());
            //没注册直接绑定
            userTempBddMapper.InsertUserTemp(userTempBdd);
            userRoleBddMapper.insertUserRole(userTempBdd.getUserid());
        } else {
            //绑定更新
            if (userTempBdd.getType() == 1) {
                userTempBddMapper.updateUserTempByWxBind(userTempBdd);
            } else if (userTempBdd.getType() == 2) {
                userTempBddMapper.updateUserTempByQQBind(userTempBdd);
            }
        }
        //更新验证码状态
        invitationBddMapper.updateInvitationBddByCodeAndPhone(params);
        return "";
    }


    /**
     * 更新用户渠道登录记录
     * @param sysUser
     */
    private void updateLoginRecord(SysUser sysUser){
        try {
            String hk = Constant.buildLoginChanelKey(sysUser.getPkid(),sysUser.getChannel());
            Long time = sysUser.getLoginTime();
            logger.debug("updateLoginRecord:[" + hk + "][" + time + "]");
            myRedisTemplate.putToHash(Constant.LOGIN_HASH_KEY, hk, time);
        }catch (Exception e){
            logger.error(e,e);
        }
    }

    /**
     * 登录用户信息校验
     * @return
     */
    public SysUser memberLogin(SysUser param){
        List<SysUser> resList = userTempBddMapper.queryUserInfo(param);
        if(resList!=null && resList.size()==1){
            //用户校验成功
            SysUser user = resList.get(0);
            Long time = System.currentTimeMillis();
            user.setLoginTime(time);//设置登录时间
            user.setXtoken(TokenUtils.buildToken(user));
            updateLoginRecord(user);
            return user;
        }else{
            //用户校验失败
            logger.debug("用户校验失败[resList:"+resList+"][param:"+param.toString()+"]");
            return null;
        }
    }

    /**
     * 用户登录
     *
     * @param userTempBdd
     * @return
     */
    public UserTempBdd queryUserTempByUserNameOrPhoneAndPassWd(UserTempBdd userTempBdd) {
        //判断前端是否已加密  IOS 密码已加密  Android 密码已加密
        if (userTempBdd.getLoginchannel().equals("1002") && Integer.parseInt(userTempBdd.getVersion()) > 10100) {
        } else if (userTempBdd.getLoginchannel().equals("1001") && Integer.parseInt(userTempBdd.getVersion()) > 10600) {
        } else {
            userTempBdd.setUserpass(DigestUtils.shaHex(userTempBdd.getUserpass()));
        }
        UserTempBdd vo = userTempBddMapper.getUserByUserNameOrPhoneAndPassWd(userTempBdd);
        //权限token
        if (vo != null && !StringUtils.isEmpty(vo.getUserid())) {
            Map<String, String> parameters = new HashedMap();
            parameters.put("name", vo.getUsername());
            parameters.put("password", vo.getUserpass());
            parameters.put("phone", vo.getUserphone());
            parameters.put("userId", vo.getUserid());
            parameters.put("date",String.valueOf(System.currentTimeMillis()));
            try {
                String secret = PropertiesUtils.getProperty("CONTENT_SECRET");
                String sign = SignConvertUtil.generateMD5Sign(secret, parameters);
                String parameterJson = JSONObject.toJSONString(parameters);
                String asB64 = getEncoder().encodeToString(parameterJson.getBytes("utf-8"));
                String xtoken = sign + "." + asB64;
                vo.setXtoken(xtoken);

            } catch (NoSuchAlgorithmException e) {
            } catch (UnsupportedEncodingException e) {
            }
        }
        return vo;
    }

    /**
     * 第三方登录
     * 1微信 2QQ
     *
     * @param userTempBdd
     * @return
     */
    public UserTempBdd queryUserTempByWXUnionIdOrQQOpenId(UserTempBdd userTempBdd) {
        UserTempBdd vo = null;
        if (userTempBdd.getType() == 1) {
            //新版登录传wxUnionId，wxopenid
            if (!StringUtils.isEmpty(userTempBdd.getWxUnionid())) {
                vo = userTempBddMapper.getUserTempByWXUnionId(userTempBdd.getWxUnionid());
                //补以前的坑
                if (vo != null) {
                    userTempBddMapper.updateWXUnionIdByWXOpenId(vo);
                }
            } else {
                //老版登录wxopenid
                vo = userTempBddMapper.getUserTempByWXOpenId(userTempBdd.getOpenid());
            }
        } else if (userTempBdd.getType() == 2) {
            vo = userTempBddMapper.getUserTempByQQOpenId(userTempBdd.getQqopenid());
        }
        //权限token
        if (vo != null) {
            Map<String, String> parameters = new HashedMap();
            parameters.put("name", vo.getUsername());
            parameters.put("password", vo.getUserpass());
            parameters.put("phone", vo.getUserphone());
            parameters.put("userId", vo.getUserid());
            parameters.put("date",String.valueOf(System.currentTimeMillis()));
            try {
                String secret = PropertiesUtils.getProperty("CONTENT_SECRET");
                String sign = SignConvertUtil.generateMD5Sign(secret, parameters);
                String parameterJson = JSONObject.toJSONString(parameters);
                String asB64 = getEncoder().encodeToString(parameterJson.getBytes("utf-8"));
                String xtoken = sign + "." + asB64;
                vo.setXtoken(xtoken);
            } catch (NoSuchAlgorithmException e) {
            } catch (UnsupportedEncodingException e) {
            }
        }
        return vo;
    }

    public String UpdatePassWdByForgetPassword(UserTempBdd userTempBdd) {
        if(userTempBddMapper.getTotalByUserPhone(userTempBdd.getUserphone()) == 0) {
            return "手机号码不存在！";
        }
        //判断验证码是否有效
        Map<String, Object> params = new HashMap<>(1);
        params.put("invitationPhone", userTempBdd.getUserphone());
        params.put("invitationCode", userTempBdd.getInvitationCode());
        InvitationBdd invitationVo = invitationBddMapper.getInvitationBddByPhoneAndCode(params);
        if (null == invitationVo) {
            return "验证码错误或无效！";
        } else if ("1".equals(invitationVo.getInvitationState())) {
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

    public Map<String, Object> weChatLogin(String code) {
        Map<String, Object> map = new HashedMap();
        if (StringUtils.isEmpty(code)) {
            map.put("code", 3);
        } else {
            //得到授权令牌
            WXAccessToken accessToken = WeChatLoginUtil.getAccessTokenByCode(code);
            //用令牌取微信用户基本信息
            WXUserInfo userInfo = WeChatLoginUtil.getWXUserInfo(accessToken.getAccessToken(), accessToken.getOpenId());
            if (userInfo != null && !StringUtils.isEmpty(userInfo.getUnionid())) {
                String wxUnionId = userInfo.getUnionid();
                UserTempBdd vo = userTempBddMapper.getUserTempByWXUnionId(wxUnionId);
                if (vo != null) {
                    Map<String, String> parameters = new HashedMap();
                    parameters.put("name", vo.getUsername());
                    parameters.put("password", vo.getUserpass());
                    parameters.put("phone", vo.getUserphone());
                    parameters.put("userId", vo.getUserid());
                    parameters.put("date",String.valueOf(System.currentTimeMillis()));
                    try {
                        String secret = PropertiesUtils.getProperty("CONTENT_SECRET");
                        String sign = SignConvertUtil.generateMD5Sign(secret, parameters);
                        String parameterJson = JSONObject.toJSONString(parameters);
                        String asB64 = getEncoder().encodeToString(parameterJson.getBytes("utf-8"));
                        String xtoken = sign + "." + asB64;
                        vo.setXtoken(xtoken);
                    } catch (Exception e) {
                    }
                    map.put("code", 1);
                    map.put("data", vo);
                } else {
                    map.put("code", 2);
                }
            }
        }
        return map;
    }

    /**
     * 验证码
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
        String flag = "";
        if (invitationBdd.getType() == 1) {
            //发送信息 注册验证码
            flag = SendMessage.registerCode(code, invitationBdd.getInvitationPhone());
        } else if (invitationBdd.getType() == 2) {
            //发送信息 找回密码验证码
            flag = SendMessage.updatePasswdCode(code, invitationBdd.getInvitationPhone());
        }
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
     * @param userTempBdd
     * @return
     */
    public UserTempBdd queryUserTempByUserPhone(UserTempBdd userTempBdd) {
        UserTempBdd vo = userTempBddMapper.getUserByUserPhone(userTempBdd.getUserphone());
        //权限token
        if (vo != null) {
            Map<String, String> parameters = new HashedMap();
            parameters.put("name", vo.getUsername());
            parameters.put("password", vo.getUserpass());
            parameters.put("phone", vo.getUserphone());
            parameters.put("userId", vo.getUserid());
            parameters.put("date",String.valueOf(System.currentTimeMillis()));
            try {
                String secret = PropertiesUtils.getProperty("CONTENT_SECRET");
                String sign = SignConvertUtil.generateMD5Sign(secret, parameters);
                String parameterJson = JSONObject.toJSONString(parameters);
                String asB64 = getEncoder().encodeToString(parameterJson.getBytes("utf-8"));
                String xtoken = sign + "." + asB64;
                vo.setXtoken(xtoken);
            } catch (NoSuchAlgorithmException e) {
            } catch (UnsupportedEncodingException e) {
            }
        }
        return vo;
    }

    public String checkUserPhone(UserTempBdd userTempBdd) {
        if(userTempBddMapper.getTotalByUserPhone(userTempBdd.getUserphone()) == 0) {
            return "手机号码不存在！";
        }
        return "";
    }

    /**
     * 新用户注册
     * @param sysUser
     * @return
     */
    public synchronized String registerUser(SysUser sysUser)throws Exception{
        //判断手机验证码是否有效
        Map<String, Object> params = new HashMap<>(1);
        params.put("invitationPhone", sysUser.getPhoneNo());
        params.put("invitationCode", sysUser.getVerifyCode());
        InvitationBdd invitationVo = invitationBddMapper.getInvitationBddByPhoneAndCode(params);
        if (null == invitationVo || "1".equals(invitationVo.getInvitationState())) {
            return Constant.ERR_VERIFY_PHONE_CODE;
        }

        //验证推荐人邀请码是否有效
        if(MyStringUtils.isNotNull(sysUser.getInviterCode())) {
            Integer count = userTempBddMapper.verifyInviterCode(sysUser);
            if (count == null || count != 1) {
                return Constant.ERR_VERIFY_IVITE_CODE;
            }
        }

        //验证登录账号（手机号）
        Map argMap = new HashMap();
        argMap.put("phoneNo",sysUser.getPhoneNo());
        argMap.put("loginName", sysUser.getLoginName());
        List<String> vList  = userTempBddMapper.verifyUserInfo(argMap);
        if(vList!=null && vList.size()>0) {
            return Constant.ERR_USER_EXIST;
        }


        String uid = CommonUtil.getUUID();
        String rId = CommonUtil.getUUID();
        sysUser.setPkid(uid);
        sysUser.setCreateBy(sysUser.getClientVersion());
        sysUser.setOwnInviteCode(constructShareCode());
        userTempBddMapper.insertUserInfo(sysUser);

        SysUserRole role = new SysUserRole();
        role.setPkid(rId);
        role.setUserId(uid);
        role.setRoleCode("normal");
        role.setCreateBy(sysUser.getClientVersion());
        userTempBddMapper.insertUserRole(role);

        //更新验证码状态
        invitationBddMapper.updateInvitationBddByCodeAndPhone(params);
        return Constant.SUCCESS_CODE;
    }

    /**
     * 邀请码暂时为实时产生，后续优化成预先产生。
     * @return
     * @throws Exception
     */
    private String constructShareCode(){
        Long unique = getIdByRedis();
        if(unique ==null){
            logger.error("邀请码生成出错：从redis获取id失败，请检查redis服务！");
            return null;
        }
        return ShareCodeUtils.idToCode(unique);
    }

    @Autowired
    private MyRedisTemplate myRedisTemplate;

    private Long getIdByRedis(){
        String rKey="inviteList";
        String hashKey = PropertiesUtils.getProperty("redis.inviteList.key");
        if(MyStringUtils.isNull(hashKey)){
            hashKey="defaultMap";
        }
        return myRedisTemplate.incrementHash(rKey,hashKey,1L);
    }
}
