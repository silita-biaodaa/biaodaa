package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
public class TbCountBidInfo {
    /**
     * 主键
     */
    private Integer pkid;

    /**
     * 公司名称
     */
    private String comName;

    /**
     * 统计日期
     */
    private String statDate;

    /**
     * 统计次数
     */
    private Integer num;

    /**
     * 统计类型(1:施工)
     */
    private Boolean type;

    /**
     * 创建时间
     */
    private Date createDate;

}