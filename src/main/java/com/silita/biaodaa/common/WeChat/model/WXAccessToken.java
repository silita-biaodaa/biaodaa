package com.silita.biaodaa.common.WeChat.model;

import java.io.Serializable;

/**
 * Created by 91567 on 2018/2/6.
 */
public class WXAccessToken implements Serializable{
    private String accessToken; //接口调用凭证
    private String expiresIn;   //access_token接口调用凭证超时时间，单位（秒）
    private String refreshToken;    //用户刷新access_token
    private String openId;  //授权用户唯一标识
    private String scope;   //用户授权的作用域，使用逗号（,）分隔
    private String unionId; //当且仅当该网站应用已获得该用户的userinfo授权时，才会出现该字段。

    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getExpiresIn() {
        return expiresIn;
    }
    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }
    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    public String getOpenId() {
        return openId;
    }
    public void setOpenId(String openId) {
        this.openId = openId;
    }
    public String getScope() {
        return scope;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public String getUnionId() {
        return unionId;
    }
    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }
}
