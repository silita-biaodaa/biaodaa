package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
public class TbProjectCompany {
    /**
     * 主键id
     */
    private String pkid;

    /**
     * 实体MD5
     */
    private String md5;

    /**
     * 企业内部id
     */
    private String innerid;

    /**
     * 项目id
     */
    private String proId;

    /**
     * 子项目id
     */
    private String pid;

    /**
     * 涉及类型，勘察单位/设计单位/监理单位/施工单位/发包单位/承包单位/联合体承包单位
     */
    private String role;

    /**
     * 企业名称
     */
    private String comName;

    /**
     * 组织机构代码
     */
    private String orgCode;

    /**
     * 所在省份
     */
    private String province;

    /**
     * 所关联的表名后缀
     */
    private String type;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;
}