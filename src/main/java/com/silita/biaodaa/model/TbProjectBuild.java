package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Map;
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

    /**非表字段**/
    /**
     * 公司名称
     */
    private String comName;

    /**
     * 施工类型
     */
    private String bidType;

    /**
     * 合同金额
     */
    private String constractAmount;

    /**
     * 记录登记时间
     */
    private String recordDate;

    /**
     * 面积
     */
    private String acreage;

    /**
     * 项目经理
     */
    private String pmName;

    /**
     * 项目经理身份证
     */
    private String pmIdCard;

    /**
     * 项目总监
     */
    private String pdName;

    /**
     * 项目总监身份证
     */
    private String pdIdCard;

    /**
     * 公司
     */
    private Map companyMap;
}