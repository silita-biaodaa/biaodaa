package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.UserTempBddMapper;
import com.silita.biaodaa.model.SysUser;
import com.silita.biaodaa.model.weixin.JsapiSignature;
import com.silita.biaodaa.model.weixin.TextMessage;
import com.silita.biaodaa.utils.HttpUtils;
import com.silita.biaodaa.utils.MessageUtil;
import com.silita.biaodaa.utils.PropertiesUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.silita.biaodaa.common.Constant.PROFIT_S_CODE_FIRST;

/**
 * Created by zhangxiahui on 17/6/13.
 */
@Service
public class WeixinService {

    Logger logger = Logger.getLogger(WeixinService.class);

    @Autowired
    private UserTempBddMapper userTempBddMapper;
    @Autowired
    private AuthorizeService authorizeService;
    @Autowired
    VipService vipService;

    /**
     * 解析微信请求并读取XML
     *
     * @param request
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static Map<String, String> readWeixinXml(HttpServletRequest request) throws IOException, DocumentException {
        Map<String, String> map = new HashMap<String, String>();
        //获取输入流
        InputStream input = request.getInputStream();
        //使用dom4j的SAXReader读取（org.dom4j.io.SAXReader;）
        SAXReader sax = new SAXReader();
        Document doc = sax.read(input);
        //获取XML数据包根元素
        Element root = doc.getRootElement();
        //得到根元素的所有子节点
        @SuppressWarnings("unchecked")
        List<Element> elementList = root.elements();
        //遍历所有节点并将其放进map
        for (Element e : elementList) {
            map.put(e.getName(), e.getText());
        }
        //释放资源
        input.close();
        input = null;
        return map;

    }

    public String processRequest(HttpServletRequest req) {
        // 解析微信传递的参数
        String str = "success";
        try {
            Map<String, String> xmlMap = readWeixinXml(req);
//            str = "请求处理异常，请稍后再试！";

            String ToUserName = xmlMap.get("ToUserName");
            String FromUserName = xmlMap.get("FromUserName");
            String MsgType = xmlMap.get("MsgType");
            logger.info("发送方账号:" + FromUserName + ",接收方账号(开发者微信号):" + ToUserName + ",消息类型:" + MsgType);

            if (MsgType.equals(MessageUtil.MESSAGG_TYPE_TEXT)) {
                // 用户发送的文本消息
                String content = xmlMap.get("Content");
                logger.info("用户：[" + FromUserName + "]发送的文本消息：" + content);
            } else if (MsgType.equals(MessageUtil.MESSAGG_TYPE_EVENT)) {
                String event = xmlMap.get("Event");
                if (event.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
                    // 订阅
                    TextMessage tm = new TextMessage();
                    tm.setToUserName(FromUserName);
                    tm.setFromUserName(ToUserName);
                    tm.setMsgType(MessageUtil.MESSAGG_TYPE_TEXT);
                    tm.setCreateTime(System.currentTimeMillis());
                    tm.setContent("你好，欢迎关注[标大大云数据]公众号！");
                    return MessageUtil.textMessageToXml(tm);
                } else if (event.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
                    // 取消订阅
                    logger.info("用户【" + FromUserName + "]取消关注了。");
                } else if (event.equals(MessageUtil.EVENT_TYPE_CLICK)) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("处理微信请求时发生异常：");
        }

        return str;
    }

    public String fetchAccessToken() {
        String token = null;
        String appid = PropertiesUtils.getProperty("appid");
        String requestUrl = PropertiesUtils.getProperty("access_token_url");
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("appid", appid);
        String parameterJson = JSONObject.toJSONString(parameter);
        logger.info("==调用fetchAccessToken入参[" + parameterJson + "]");
        String result = HttpUtils.connectURL(requestUrl, parameterJson, "POST");
        logger.info("==调用fetchAccessToken返回[" + result + "]");
        JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
        Integer status = jsonObject.getInteger("status");
        if (status != null && status.intValue() == 1) {
            JSONObject accessToken = (JSONObject) jsonObject.get("accessToken");
            if (accessToken != null && accessToken.getString("accessToken") != null) {
                token = accessToken.getString("accessToken");
            }
        }
        return token;
    }

    public JsapiSignature fetchJsapiSignature(String url) {
        JsapiSignature signature = null;
        String appid = PropertiesUtils.getProperty("appid");
        String requestUrl = PropertiesUtils.getProperty("signature_url");
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("appid", appid);
        parameter.put("url", url);
        String parameterJson = JSONObject.toJSONString(parameter);
        logger.info("==调用JsapiSignaturer入参[" + parameterJson + "]");
        String result = HttpUtils.connectURL(requestUrl, parameterJson, "POST");
        logger.info("==调用JsapiSignature返回[" + result + "]");
        JSONObject jsonObject = (JSONObject) JSONObject.parse(result);
        Integer status = jsonObject.getInteger("status");
        if (status != null && status.intValue() == 1) {
            JSONObject signatureJSON = (JSONObject) jsonObject.get("jsapiSignature");
            signature = new JsapiSignature();
            signature.setSignature(signatureJSON.getString("signature"));
            signature.setTimestamp(signatureJSON.getString("timestamp"));
            signature.setUrl(signatureJSON.getString("url"));
            signature.setJsapiTicket(signatureJSON.getString("jsapiTicket"));
            signature.setNoncestr(signatureJSON.getString("noncestr"));
        }
        return signature;
    }

    /**
     * 用户绑定
     *
     * @param param
     * @return
     */
    public Map<String, Object> bindingUser(Map<String, Object> param) {
        Map<String, Object> result = new HashedMap(3);
        //判断用户是否注册
        String phone = MapUtils.getString(param, "phone");
        List<SysUser> sysUserList = userTempBddMapper.queryUserByPhoneNo(phone);
        if (null == sysUserList || sysUserList.size() <= 0) {
            result.put("code", Constant.HINT_NOT_REGIST);
            result.put("msg", "手机号还未注册");
            return result;
        }
        //登录(验证码校验)
        SysUser sysUser = new SysUser();
        sysUser.setPhoneNo(phone);
        sysUser.setVerifyCode(MapUtils.getString(param, "verifyCode"));
        String checkCode = authorizeService.verifyInviterCode(sysUser);
        if (StringUtils.isNotEmpty(checkCode)) {
            result.put("code", checkCode);
            result.put("msg", "验证码失效或错误！");
            return result;
        }
        //绑定
        String code = MapUtils.getString(param, "code");
        String unionId = getUnionId(code);
        if (StringUtils.isNotEmpty(unionId)) {
            //赠送会员天数并绑定
            String userId = sysUserList.get(0).getPkid();
            bingProfit(userId, unionId);
            //设置token
            sysUser.setChannel("weChat");
            SysUser uers = authorizeService.memberLogin(sysUser);
            if (null != uers && !uers.getEnable()) {
                result.put("code", Constant.ERR_LOCK_USER);
                result.put("msg", "用户已被锁定!");
                return result;
            }
            result.put("data", uers);
            result.put("code", Constant.SUCCESS_CODE);
            result.put("msg", "用户登录成功！");
            return result;
        } else {
            result.put("code", Constant.ERR_NOT_FOUND);
            result.put("msg", "用户未关注公众号！");
            return result;
        }
    }

    /**
     * 用户登录
     *
     * @param param
     * @return
     */
    public Map<String, Object> loginUser(Map<String, Object> param) {
        Map<String, Object> result = new HashedMap(3);
        String weChat = MapUtils.getString(param, "code");
        String unionId = this.getUnionId(weChat);
        if (StringUtils.isEmpty(unionId)) {
            result.put("code", Constant.ERR_NOT_FOUND);
            result.put("msg", "用户未关注公众号！");
            return result;
        }
        SysUser userInfo = authorizeService.memberLoginByUnionId(unionId);
        if (userInfo != null && userInfo.getEnable()) {
            result.put("code", Constant.SUCCESS_CODE);
            result.put("msg", "用户登录成功！");
            result.put("data", userInfo);
            return result;
        } else if (null != userInfo && !userInfo.getEnable()) {
            result.put("code", Constant.ERR_LOCK_USER);
            result.put("msg", "用户已被锁定!");
            return result;
        } else {
            result.put("code", Constant.HINT_NOT_REGIST);
            result.put("msg", "用户还未注册/或未绑定账号");
            return result;
        }
    }

    /**
     * 解绑用户
     *
     * @return
     */
    public void untieUser() {
        String userId = VisitInfoHolder.getUid();
        Map<String, Object> param = new HashedMap(2);
        param.put("userId", userId);
        param.put("state", "1");
        userTempBddMapper.updateRelUserInfo(param);
    }

    /**
     * 注册并绑定
     *
     * @param param
     * @return
     */
    public Map<String, Object> registerBindingUser(Map<String, Object> param) throws Exception {
        Map<String, Object> result = new HashedMap(3);
        String weChatCode = MapUtils.getString(param, "code");
        //公众号绑定
        String unionId = this.getUnionId(weChatCode);
        if (StringUtils.isEmpty(unionId)) {
            result.put("code", Constant.ERR_NOT_FOUND);
            result.put("msg", "用户未关注公众号！");
            return result;
        }
        SysUser user = new SysUser();
        user.setChannel("weChat");
        user.setVerifyCode(MapUtils.getString(param, "verifyCode"));
        user.setPhoneNo(MapUtils.getString(param, "phone"));
        user.setLoginPwd(MapUtils.getString(param, "loginPwd"));
        String checkCode = authorizeService.registerUser(user);
        result.put("code", checkCode);
        if (checkCode.equals(Constant.ERR_VERIFY_PHONE_CODE)) {
            result.put("msg", "验证码失效或错误！");
            return result;
        } else if (checkCode.equals(Constant.ERR_USER_EXIST)) {
            result.put("msg", "手机号已注册，请立即登录！");
            return result;
        } else if (checkCode.equals(Constant.SUCCESS_CODE)) {
            //赠送会员天数并绑定
            String userId = user.getPkid();
            bingProfit(userId, unionId);
            firstProfitDeliver(user);
            SysUser uers = authorizeService.memberLogin(user);
            result.put("code", Constant.SUCCESS_CODE);
            result.put("data", uers);
            result.put("msg", "用户注册成功！");
            return result;
        } else {
            result.put("msg", "未知错误码！");
            return result;
        }
    }

    /**
     * 绑定并赠送天数
     *
     * @param userId
     * @param unionId
     */
    private void bingProfit(String userId, String unionId) {
        //判断是否绑定过
        int count = userTempBddMapper.queryRelUserInfoCount(userId);
        if (count <= 0) {
            firstBindionProfitDeliver(userId, "weChat", Constant.PROFIT_S_CODE_BINDNG);
        }
        Map<String, Object> valMap = new HashedMap(2);
        valMap.put("userId", userId);
        valMap.put("unionId", unionId);
        //添加绑定记录
        if (count > 0) {
            userTempBddMapper.updateRelUserInfo(valMap);
        } else {
            userTempBddMapper.insertRelUserInfo(valMap);
        }
    }

    /**
     * 获取用户信息
     *
     * @param code 微信标识code
     * @return
     */
    private String getUser(String code) {
        Map<String, Object> param = new HashedMap(2) {{
            put("code", code);
        }};
        String appid = PropertiesUtils.getProperty("appid");
        param.put("appid", appid);
        String userUrl = PropertiesUtils.getProperty("user_detail");
        String parameterJson = JSONObject.toJSONString(param);
        logger.info("==获取用户信息URL[" + userUrl + "]");
        String result = HttpUtils.connectURL(userUrl, parameterJson, "POST");
        logger.info("==获取用户信息返回[" + result + "]");
        if (StringUtils.isEmpty(result)) {
            return null;
        }
        return result;
    }


    /**
     * 首次绑定
     */
    private void firstBindionProfitDeliver(String userId, String channel, String code) {
        Integer hisCount = vipService.queryUserProfitCount(code, userId);
        if (hisCount == 0) {
            String errMsg = vipService.addUserProfit(channel, userId, code);
            if (errMsg != null) {
                logger.error("会员权益赠送出错[userId:" + userId + "][sCode:" + code + "]：" + errMsg);
            }
        } else {
            logger.debug("权益赠送取消，已存在权益收益！");
        }
    }

    /**
     * 注册赠送
     *
     * @param sysUser
     */
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
     * 获取unionId
     *
     * @param code
     * @return
     */
    private String getUnionId(String code) {
        String user = this.getUser(code);
        JSONObject resultJson = JSONObject.parseObject(user);
        if ("1".equals(resultJson.getInteger("code"))) {
            Map<String, Object> dataMap = (Map<String, Object>) resultJson.get("data");
            String unionId = MapUtils.getString(dataMap, "unionId");
            return unionId;
        }
        return null;
    }
}
