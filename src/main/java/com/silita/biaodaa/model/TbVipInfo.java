package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 会员信息主表
 */
@Getter
@Setter
public class TbVipInfo {
    private String vId;
    private String userId;
    private int level;
    private String remark;
    private String permissions;
    private String roleCode;
    private Date expiredDate;
    private String created;
    private String createBy;
    private String updated;
    private String updateBy;
}
