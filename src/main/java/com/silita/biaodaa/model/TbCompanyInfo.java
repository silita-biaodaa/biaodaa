package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbCompanyInfo {
    /**
     * 主键id
     */
    private String pkid;

    /**
     * 公司名称
     */
    private String comName;

    /**
     * 统一社会信用代码
     */
    private String creditCode;

    /**
     * 注册号
     */
    private String businessNum;

    /**
     * 组织机构代码
     */
    private String orgCode;

    /**
     * 省份
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 公司类型
     */
    private String comType;

    /**
     * 行业
     */
    private String trade;

    /**
     * 法人
     */
    private String legalPerson;

    /**
     * 注册资本 
     */
    private String regisCapital;

    /**
     * 注册时间
     */
    private String regisDate;

    /**
     * 营业开始时间
     */
    private String startDate;

    /**
     * 营业结束时间
     */
    private String endDate;

    /**
     * 核准机关
     */
    private String checkOrg;

    /**
     * 核准日期
     */
    private String checkDate;

    /**
     * 电话
     */
    private String phone;

    /**
     * 公司邮箱
     */
    private String email;

    /**
     * 公司网址
     */
    private String comUrl;

    /**
     * 企业状态
     */
    private String status;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    /**
     * 经营范围
     */
    private String scope;

    /**
     * 地址
     */
    private String comAddress;
}