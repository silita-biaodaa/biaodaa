package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbPersonQualification {
    /**
     * 人员资质主键
     */
    private Integer pkid;

    /**
     * 网站内部ID
     */
    private String innerid;

    /**
     * 注册类别
     */
    private String category;

    /**
     * 证书编号
     */
    private String certNo;

    /**
     * 执业印章号
     */
    private String sealNo;

    /**
     * 注册专业
     */
    private String major;

    /**
     * 注册日期
     */
    private String certDate;

    /**
     * 有效期
     */
    private String validDate;

    /**
     * 人员ID
     */
    private Integer perId;

    /**
     * 企业ID
     */
    private Integer comId;

    /**
     * 单位名称
     */
    private String comName;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    /**
     * 抓取URL
     */
    private String url;

    //人员姓名
    private String name;
}