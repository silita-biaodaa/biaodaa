package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class TbProjectCompletion {
    /**
     * 主键id
     */
    private String pkid;

    /**
     * 实体MD5
     */
    private String md5;

    /**
     * 项目id
     */
    private String proId;

    /**
     * 竣工备案编号
     */
    private String code;

    /**
     * 省级竣工备案编号
     */
    private String provCode;

    /**
     * 实际造价
     */
    private String cost;

    /**
     * 实际面积
     */
    private String area;

    /**
     * 实际建设规模
     */
    private String buildScale;

    /**
     * 结构体系
     */
    private String struct;

    /**
     * 实际开工日期
     */
    private String buildStart;

    /**
     * 实际竣工验收日期
     */
    private String buildEnd;

    /**
     * 记录登记时间
     */
    private String recordDate;

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

    //TODO:非表字段
    private List companys;

    /**
     * 结构体系
     */
    private String structs;

    public void setStructs(){
        this.structs = this.struct;
    }
    public String getStructs(){
        return this.struct;
    }

}