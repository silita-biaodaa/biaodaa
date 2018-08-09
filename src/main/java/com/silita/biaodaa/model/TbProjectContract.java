package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Setter
@Getter
public class TbProjectContract {
    /**
     * 项目下的合同备案自增id
     */
    private String pkid;

    /**
     * 项目id
     */
    private String proId;

    /**
     * 合同备案编号
     */
    private String recordCode;

    /**
     * 省级合同备案编号
     */
    private String provRecordCode;

    /**
     * 合同编号
     */
    private String code;

    /**
     * 合同分类
     */
    private String category;

    /**
     * 合同类别
     */
    private String type;

    /**
     * 合同金额
     */
    private String amount;

    /**
     * 建设规模
     */
    private String buildScale;

    /**
     * 合同签订日期
     */
    private String signDate;

    /**
     * 记录登记时间
     */
    private String recordDate;

    /**
     * 创建时间
     */
    private Date created;

    /*非表字段*/
    /**
     * 承包单位
     */
    private  String contractComName;

    /**
     * 承包单位组织机构代码
     */
    private String contractOrgCode;

    /**
     * 发包单位
     */
    private  String letContractComName;

    /**
     * 发包单位组织机构代码
     */
    private String letOrgCode;

    /**
     * 联合体承包单位
     */
    private String jointComName;

    /**
     * 联合体承包单位组织机构代码
     */
    private String jointOrgCode;
}