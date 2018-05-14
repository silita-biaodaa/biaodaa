package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Setter
@Getter
public class TbProjectSupervision {
    /**
     * 监理自增主键
     */
    private Integer pkid;

    /**
     * 监理项目内部ID
     */
    private String jlbdxh;

    /**
     * 项目ID
     */
    private Integer proId;

    /**
     * 项目名称
     */
    private String proName;

    /**
     * 项目类型
     */
    private String proType;

    /**
     * 企业ID
     */
    private Integer comId;

    /**
     * 监理单位
     */
    private String superOrg;

    /**
     * 合同时间
     */
    private String contractDate;

    /**
     * 合同价格
     */
    private String contractPrice;

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

    /**
     * 中标备案情况
     */
    private String bidRemark;

    /**
     * 合同备案情况
     */
    private String contractRemark;

}