package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbPhoneAddressBook {
    /**
     * 主键id
     */
    private Integer pkid;

    /**
     * 设备id
     */
    private String deviceId;

    /**
     * 手机联系人
     */
    private String phoneName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 登录用户手机号
     */
    private String userPhone;

    /**
     * 来源(1：Android；2：iOS)
     */
    private String sourceFrom;

    /**
     * 创建时间
     */
    private Date createdDate;
}