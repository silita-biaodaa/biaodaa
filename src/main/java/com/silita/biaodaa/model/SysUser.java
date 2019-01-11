package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.security.Timestamp;
@Getter
@Setter
public class SysUser {
    private String pkid;
    private String login_name;
    private String login_pwd;
    private String user_name;
    private Integer sex;//0:女；1:男；2：其他
    private String phone_no;
    private String channel;
    private String nike_name;
    private int cert_type;
    private String cert_no;
    private String email;
    private String birth_year;
    private String in_city;
    private String in_company;
    private String position;
    private String image_url;
    private String wx_union_id;
    private String wx_open_id;
    private String qq_open_id;
    private boolean enable;
    private Timestamp created;
    private String create_by;
    private Timestamp updated;
    private String update_by;
    private String inviter_code;
    private String own_invite_code;
    private String role_code;
    private String role_name;
    private String permissions;

    private String xtoken;
    private String clientVersion;
    private String deviceId;

    private String vip_name;
    private String expired_date;
    private String level;

    private String verifyCode;//手机验证码
    private Long loginTime;//登录时间

}