package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CertBasic implements Serializable {
    private Integer id;

    private String srcuuid;

    /**
     * 注册号
     */
    private String registerno;

    /**
     * 企业名称
     */
    private String companyname;

    /**
     * 类型
     */
    private String companytype;

    /**
     * 法定人代表
     */
    private String legalperson;

    /**
     * 注册资本
     */
    private String registerfund;

    /**
     * 成立日期
     */
    private String setupdate;

    /**
     * 住所
     */
    private String domicile;

    /**
     * 经营日期自
     */
    private String runstartdate;

    /**
     * 经营日期至
     */
    private String runenddate;

    /**
     * 经营范围
     */
    private String runscope;

    /**
     * 登记机关
     */
    private String registeroffice;

    /**
     * 核准日期
     */
    private String checkdate;

    /**
     * 登记状态
     */
    private String registerstatus;
}