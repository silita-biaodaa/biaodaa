package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbBidCompute {
    /**
     * 主键id
     */
    private Integer pkid;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 项目名称
     */
    private String proName;

    /**
     * 项目标段名称
     */
    private String secName;

    /**
     * 项目类型
     */
    private String proType;

    /**
     * 评标办法
     */
    private String bidWay;

    /**
     * 开标时间
     */
    private String bidDate;

    /**
     * 状态;默认为1
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 参数模板
     */
    private String credTmp;
}