package com.silita.biaodaa.model.es;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Law {

    /**
     * 主键id
     */
    private String id;

    /**
     * 关键词，会存企业名称或行贿、受贿等关键词
     */
    private String keyword;

    /**
     * 文书url
     */
    private String url;

    /**
     * 标题
     */
    private String title;

    /**
     * 正文
     */
    private String content;

    /**
     * 案号
     */
    private String caseNo;

    /**
     * 法院
     */
    private String court;

    /**
     * 裁判日期
     */
    private Long date;

    /**
     * 裁判日期 String
     */
    private String dateStr;

    /**
     * 案由
     */
    private String cause;

    /**
     * 案件类型
     */
    private String type;

    /**
     * 程序，一审二审这些
     */
    private String procedure;

    /**
     * 标签
     */
    private String tags;

    /**
     * 原告
     */
    private String accuser;

    /**
     * 被告
     */
    private String accused;

    /**
     * 原审被告
     */
    private String originalAccused;

    /**
     * 上诉人
     */
    private String appellor;

    /**
     * 被上诉人
     */
    private String appellee;

    /**
     * 申请人
     */
    private String applicant;

    /**
     * 被申请人
     */
    private String respondent;

    /**
     * 申请执行人
     */
    private String executionApplicant;

    /**
     * 被执行人
     */
    private String executionedApplicant;

    /**
     * 原审第三人
     */
    private String originalThirdMan;

    /**
     * 委托代理人
     */
    private String entrustedAgent;

    /**
     * 第三人
     */
    private String thirdMan;

    /**
     * 律师
     */
    private String lawyer;

    /**
     * 实习律师
     */
    private String practiceLawyer;

    /**
     * 法律工作者
     */
    private String paralegal;

    /**
     * 律师事务所
     */
    private String lawOffice;

    /**
     * 审判长
     */
    private String presidingJudge;

    /**
     * 审判员
     */
    private String judicialOfficer;

    /**
     * 书记员
     */
    private String courtClerk;

    /**
     * 档案编号
     */
    private String number;
}
