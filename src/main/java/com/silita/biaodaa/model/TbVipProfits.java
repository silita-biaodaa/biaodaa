package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.security.Timestamp;

/**
 * 用户收益实体
 */
@Getter
@Setter
public class TbVipProfits {
    private String vProfitsId;
    private String settingsCode;
    private String vId;
    private String inviterCode;
    private String hisExpiredDate;
    private Timestamp created;
    private String createBy;
    private String desc;
    private String vipDays;
}
