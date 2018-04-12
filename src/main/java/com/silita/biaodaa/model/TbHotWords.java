package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbHotWords {
    /**
     * 主键
     */
    private Integer pkid;

    /**
     * 热搜词
     */
    private String word;

    /**
     * 0、企业 2、人员
     */
    private Integer type;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 更新时间
     */
    private Date updateDate;
}