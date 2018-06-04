package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class CertUndesirable {

    private Integer id;

    /**
     * cert_src.uuid
     */
    private String srcuuid;

    /**
     * 项目名称
     */
    private String projectname;

    /**
     * 行为代码
     */
    private String actioncode;

    /**
     * 不良行为内容
     */
    private String badbehaviorcontent;

    /**
     * 性质
     */
    private String nature;

    /**
     * 相关人员姓名
     */
    private String persionname;

    /**
     * 相关人员注册号
     */
    private String persionid;

    /**
     * 相关人员岗位
     */
    private String persionposition;

    /**
     * 发布日期
     */
    private String publishdate;

    /**
     * 发布地点
     */
    private String publishsite;

    /**
     * 处罚决定
     */
    private String punishmentdecision;

    /**
     * 有效期
     */
    private String validitydate;

    /**
     * rize_zh.uuid
     */
    private String zhuuid;

    /**
     * 0抓取,1导入
     */
    private Integer datatype;

    /**
     * 创建时间
     */
    private Date createdate;

    private BigDecimal score;
}