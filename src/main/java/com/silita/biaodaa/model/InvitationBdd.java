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
}