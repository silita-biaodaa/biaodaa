package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import javax.persistence.*;

@Getter
@Setter
public class TbProjectZhaotoubiao {
    /**
     * 项目下的招投标详情自增id
     */
    private Integer pkid;

    /**
     * 项目id
     */
    private String proId;

    /**
     * 中标通知书编号
     */
    private String zhongbiaoCode;

    /**
     * 省级中标通知书编号
     */
    private String provZhongbiaoCode;

    /**
     * 招标类型
     */
    private String zhaobiaoType;

    /**
     * 招标方式
     */
    private String zhaobiaoWay;

    /**
     * 中标日期
     */
    private String zhongbiaoDate;

    /**
     * 中标金额
     */
    private String zhongbiaoAmount;

    /**
     * 招标代理单位名称
     */
    private String zhaobiaoAgencyCompany;

    /**
     * 招标代理单位组织机构代码
     */
    private String zhaobiaoCompanyCode;

    /**
     * 中标单位名称
     */
    private String zhongbiaoCompany;

    /**
     * 中标单位组织机构代码
     */
    private String zhongbiaoCompanyCode;

    /**
     * 记录登记时间
     */
    private String recordDate;

    /**
     * 创建时间
     */
    private Date created;

    /**非表字段**/

    /**
     * 面积
     */
    private String acreage;

    /**
     * 建设规模
     */
    private String proScope;

    /**
     * 人员姓名
     */
    private String name;

    /**
     * 身份证
     */
    private String idCard;

    /**
     * 人员列表
     */
    private List personList;
}