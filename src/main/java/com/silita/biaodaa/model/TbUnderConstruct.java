package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class TbUnderConstruct {
    /**
     * 主键id
     */
    private String pkid;

    /**
     * 内部id
     */
    private String innerid;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号码
     */
    private String idCard;

    /**
     * 人员类别
     */
    private String type;

    /**
     * 市
     */
    private String city;

    /**
     * 押证日期
     */
    private String date;

    /**
     * 县区
     */
    private String county;

    /**
     * 建设单位
     */
    private String proOrg;

    /**
     * 工程名称
     */
    private String proName;

    /**
     * 单位名称
     */
    private String unitOrg;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;
}