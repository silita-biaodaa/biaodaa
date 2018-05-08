package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
public class TbProjectDesign {
    /**
     * 勘查设计项目自增ID
     */
    private Integer pkid;

    /**
     * 网站内部ID
     */
    private String sgtxh;

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
     * 勘查单位
     */
    private String exploreOrg;

    /**
     * 设计单位
     */
    private String designOrg;

    /**
     * 施工图审查单位
     */
    private String checkOrg;

    /**
     * 省级施工图审查合格书编号
     */
    private String checkNo;

    /**
     * 施工图审查合格书编号
     */
    private String checkNumber;

    /**
     * 施工图审查完成日期
     */
    private String checkFinishDate;

    /**
     * 施工图审查人
     */
    private String checkPerson;

    /**
     * 勘查OR设计类型，勘查：explore；设计：design
     */
    private String type;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

    /**
     * 项目规模
     */
    private String proScope;

    /**
     * 审查机构代码
     */
    private String checkOrgCode;

    /**
     * 设计单位代码
     */
    private String designOrgCode;

    /**
     * 勘查单位代码
     */
    private String exploreOrgCode;

    /**
     * 设计单位所在地
     */
    private String regisAddressDesign;

    /**
     * 勘察单位所在地
     */
    private String regisAddressExplore;

    /**
     * 设计单位所在地(省)
     */
    private String designProvince;

    /**
     * 勘察单位所在地(省)
     */
    private String exploreProvince;
}