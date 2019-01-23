package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 会员权限变更记录实体
 */
@Getter
@Setter
public class TbVipRightsChanges {
    private String vRightsId;
    private String vId;
    private String vFeeStdId;
    private int rightsNum;
    private String modType;
    private int increaseDays;
    private Date hisExpiredDate;
    private String reason;
    private String created;
    private String createBy;
    private String updated;
    private String updateBy;
}
