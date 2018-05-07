package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
public class TbProjectBuild {
    /**
     * 施工自增ID
     */
    private Integer pkid;

    /**
     * 网站内部ID
     */
    private String bdxh;

    /**
     * 关联项目ID
     */
    private Integer proId;

    /**
     * 关联项目名称
     */
    private String proName;

    /**
     * 关联的工程类别
     */
    private String proType;

    /**
     * 企业ID
     */
    private Integer comId;

    /**
     * 标段名称
     */
    private String bName;

    /**
     * 施工单位
     */
    private String bOrg;

    /**
     * 中标备案情况
     */
    private String bidRemark;

    /**
     * 中标金额
     */
    private String bidPrice;

    /**
     * 合同备案情况
     */
    private String contractRemark;

    /**
     * 省级施工许可证号
     */
    private String bLicence;

    /**
     * 施工许可证号
     */
    private String buildLicence;

    /**
     * 施工图审查合格书编号
     */
    private String reviewNumber;

    /**
     * 合同分类
     */
    private String contractCategory;

    /**
     * 施工许可证日期
     */
    private String licenceDate;

    /**
     * 竣工验收备案情况
     */
    private String completeRemark;

    /**
     * 竣工时间
     */
    private String completeDate;

    /**
     * 分包合同
     */
    private String subContrace;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    /**
     * 标段规模
     */
    private String bScope;

}