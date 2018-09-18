package com.silita.biaodaa.model.es;

import com.silita.biaodaa.elastic.Annotation.Filed;
import com.silita.biaodaa.elastic.Enum.FieldType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompanyInfoEs {

    /**
     * 主键id
     */
    @Filed(type = FieldType.keyword)
    private String pkid;

    /**
     * 公司名称
     */
    @Filed(type = FieldType.keyword)
    private String comName;

    /**
     * 统一社会信用代码
     */
    @Filed(type = FieldType.keyword)
    private String creditCode;

    /**
     * 注册号
     */
    @Filed(type = FieldType.keyword)
    private String businessNum;

    /**
     * 组织机构代码
     */
    @Filed(type = FieldType.keyword)
    private String orgCode;

    /**
     * 省份
     */
    @Filed(type = FieldType.keyword)
    private String province;

    /**
     * 市
     */
    @Filed(type = FieldType.keyword)
    private String city;

    /**
     * 公司类型
     */
    @Filed(type = FieldType.keyword)
    private String comType;

    /**
     * 行业
     */
    @Filed(type = FieldType.keyword)
    private String trade;

    /**
     * 法人
     */
    @Filed(type = FieldType.keyword)
    private String legalPerson;

    /**
     * 注册资本 
     */
    @Filed(type = FieldType.keyword)
    private String regisCapital;

    /**
     * 注册时间
     */
    @Filed(type = FieldType.keyword)
    private String regisDate;

    /**
     * 营业开始时间
     */
    @Filed(type = FieldType.keyword)
    private String startDate;

    /**
     * 营业结束时间
     */
    @Filed(type = FieldType.keyword)
    private String endDate;

    /**
     * 核准机关
     */
    @Filed(type = FieldType.keyword)
    private String checkOrg;

    /**
     * 核准日期
     */
    @Filed(type = FieldType.keyword)
    private String checkDate;

    /**
     * 电话
     */
    @Filed(type = FieldType.keyword)
    private String phone;

    /**
     * 公司邮箱
     */
    @Filed(type = FieldType.keyword)
    private String email;

    /**
     * 公司网址
     */
    @Filed(type = FieldType.keyword)
    private String comUrl;

    /**
     * 企业状态
     */
    @Filed(type = FieldType.keyword)
    private String status;

    /**
     * 经营范围
     */
    @Filed(type = FieldType.keyword)
    private String scope;

    /**
     * 地址
     */
    @Filed(type = FieldType.keyword)
    private String comAddress;

    /**
     * tab
     */
    @Filed(type = FieldType.keyword)
    private String tabCode;
}