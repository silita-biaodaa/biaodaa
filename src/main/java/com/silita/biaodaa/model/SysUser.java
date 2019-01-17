package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.security.Timestamp;
@Getter
@Setter
public class SysUser {
    private String pkid;
    private String loginName;
    private String loginPwd;
    private String userName;
    private Integer sex;//0:女；1:男；2：其他
    private String phoneNo;
    private String channel;
    private String nikeName;
    private int certType;
    private String certNo;
    private String email;
    private String birthYear;
    private String inCity;
    private String inCompany;
    private String position;
    private String imageUrl;
    private String wxUnionId;
    private String wxOpenId;
    private String qqOpenId;
    private boolean enable;
    private Timestamp created;
    private String createBy;
    private Timestamp updated;
    private String updateBy;
    private String inviterCode;
    private String ownInviteCode;
    private String roleCode;
    private String roleName;
    private String permissions;

    private String xtoken;
    private String clientVersion;
    private String deviceId;

    private String expiredDate;
    private String level;

    private String verifyCode;//手机验证码
    private Long loginTime;//登录时间

}