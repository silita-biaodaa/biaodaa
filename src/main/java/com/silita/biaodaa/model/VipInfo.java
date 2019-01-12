package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by dh on 2019/1/4.
 */
@Getter
@Setter
public class VipInfo {
    private String vId;
    private String userId;
    private String level;
    private String remark;
    private String expiredDate;
    private String created;
    private String createBy;
    private String updated;
    private String updateBy;
}
