package com.silita.biaodaa.common.WeChat.util;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.common.WeChat.model.WXAccessToken;
import com.silita.biaodaa.common.WeChat.model.WXUserInfo;

import java.util.HashMap;

/**
 * 微信扫描登录工具类
 * Created by 91567 on 2018/2/6.
 */
public class WeChatLoginUtil {
    public final static String APPID = "wxcfaea301018d9721";
    public final static String APPSECRET = "167a78379c81b04f4faaeb2ed03ccaee";

    public final static String ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    public final static String REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
    public final static String CHECK_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/auth?access_token=ACCESS_TOKEN&openid=OPENID";
    public final static String GET_USERINFO = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID";


    /**
     * 通过code获取access_token
     * @param code 扫描确认登录后，回掉路径中带的参数
     * @return
     */
    public static WXAccessToken getAccessTokenByCode(String code) {
        String appID = APPID;
        String appSecret = APPSECRET;
        String getTokenByCodeUrl = ACCESS_TOKEN;
        getTokenByCodeUrl = getTokenByCodeUrl.replace("APPID", appID).replace("SECRET", appSecret).replace("CODE", code);
        JSONObject jsonObject = CommonUtil.httpsRequest(getTokenByCodeUrl, EnumMethod.GET.name(), null);

        WXAccessToken accessToken = new WXAccessToken();
        accessToken.setAccessToken(jsonObject.getString("access_token"));
        accessToken.setExpiresIn(jsonObject.getString("expires_in"));
        accessToken.setRefreshToken(jsonObject.getString("refresh_token"));
        accessToken.setOpenId(jsonObject.getString("openid"));
        accessToken.setScope(jsonObject.getString("scope"));
//        accessToken.setUnionId(jsonObject.getString("unionid"));
        return accessToken;
    }

    /**
     * 根据refresh_token刷新、延时AccessToken
     * @param refresh_token
     * @return
     */
    public static WXAccessToken refreshToken (String refresh_token) {
        String appId = APPID;
        String url = REFRESH_TOKEN;
        url = url.replace("APPID", appId).replace("REFRESH_TOKEN", refresh_token);
        JSONObject jsonObject = CommonUtil.httpsRequest(url, EnumMethod.GET.name(), null);

        WXAccessToken accessToken = new WXAccessToken();
        accessToken.setAccessToken(jsonObject.getString("access_token"));
        accessToken.setExpiresIn(jsonObject.getString("expires_in"));
        accessToken.setRefreshToken(jsonObject.getString("refresh_token"));
        accessToken.setOpenId(jsonObject.getString("openid"));
        accessToken.setScope(jsonObject.getString("scope"));
        return accessToken;
    }

    /**
     * 检验授权凭证（access_token）是否有效
     * @param accessToken
     * @param openid
     * @return
     */
    public static HashMap<String, String> checkAccessToken(String accessToken, String openid) {
        String url = CHECK_ACCESS_TOKEN;
        url = url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
        JSONObject jsonObject = CommonUtil.httpsRequest(url, EnumMethod.GET.name(), null);

        HashMap map = new HashMap<String, String>();
        map.put("errcode", jsonObject.getString("errcode"));
        map.put("errmsg", jsonObject.getString("errmsg"));
        return map;
    }

    /**
     * 取得微信用户基本信息
     * @param accessToken
     * @param openid
     * @return
     */
    public static WXUserInfo getWXUserInfo(String accessToken, String openid) {
        String url = GET_USERINFO;
        url = url.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
        JSONObject jsonObject = CommonUtil.httpsRequest(url, EnumMethod.GET.name(), null);

        WXUserInfo wxUserInfo = new WXUserInfo();
        wxUserInfo.setOpenid(jsonObject.getString("openid"));
        wxUserInfo.setNickname(jsonObject.getString("nickname"));
        wxUserInfo.setSex(jsonObject.getString("sex"));
        wxUserInfo.setProvince(jsonObject.getString("province"));
        wxUserInfo.setCity(jsonObject.getString("city"));
        wxUserInfo.setCountry(jsonObject.getString("country"));
        wxUserInfo.setHeadimgurl(jsonObject.getString("headimgurl"));
        wxUserInfo.setPrivilege(jsonObject.getString("privilege"));
        wxUserInfo.setUnionid(jsonObject.getString("unionid"));
        return wxUserInfo;
    }

}
