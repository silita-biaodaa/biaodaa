package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class InvitationBdd {

    private Integer invitationId;

    private String userid;

    private String invitationIp;

    private String invitationPhone;

    private String invitationCode;

    private String invitationState;

    private Date createdate;

    //用于区分注册、找回密码等短信类型
    private Integer type;

    private String msgTemplate;//短信模板编码
}