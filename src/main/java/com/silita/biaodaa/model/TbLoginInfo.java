package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TbLoginInfo {
    /**
     * 主键id
     */
    private String pkid;

    /**
     * 登录用户名
     */
    private String loginName;

    /**
     * 登录手机号
     */
    private String loginTel;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 创建时间
     */
    private Date createdDate;

    /**
     * 修改时间
     */
    private Date updatedDate;
}