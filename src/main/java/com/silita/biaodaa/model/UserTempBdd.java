package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UserTempBdd {
    private Integer id;

    private String userid;

    private String username;

    private String userpass;

    private String companyname;

    private String userphone;

    private String imgurl;

    private Integer roleid;

    private String rolecode;

    private String openid;

    private String wxopenid;

    private String qqopenid;

    private String gender;

    private String nickname;

    private String mailbox;

    private String loginchannel;

    private Date registrationtime;

    /**
     * 微信开发平台唯一id
     */
    private String wxUnionid;
    /**
     * 验证码
     */
    private String invitationCode;
    //版本号
    private String version;
}