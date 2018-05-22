package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbClickStatistics {
    /**
     * 主键id
     */
    private Integer pkid;

    /**
     * 点击类型
     */
    private String type;

    /**
     * 公告内部id
     */
    private Integer innertId;

    /**
     * 点击此处
     */
    private Integer clickCount;

    /**
     * 省份路由
     */
    private String source;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

}