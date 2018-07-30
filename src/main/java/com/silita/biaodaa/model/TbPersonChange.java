package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
public class TbPersonChange {
    /**
     * 自增主键
     */
    private String pkid;

    /**
     * 单位名称
     */
    private String comName;

    /**
     * 注册专业
     */
    private String major;

    /**
     * 变更日期
     */
    private String changeDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 人员ID
     */
    private Integer perId;

    /**
     * 唯一标识用于确定人员唯一
     */
    private String flag;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

}