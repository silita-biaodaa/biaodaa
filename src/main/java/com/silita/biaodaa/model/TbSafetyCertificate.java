package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TbSafetyCertificate {
    /**
     * 安全许可证自增ID
     */
    private Integer pkid;

    /**
     * 企业名称
     */
    private String comName;

    /**
     * 安全生产许可证
     */
    private String certNo;

    /**
     * 发证日期
     */
    private String certDate;

    /**
     * 有效期
     */
    private String validDate;

    /**
     * 创建时间
     */
    private Date created;

    /**
     * 更新时间
     */
    private Date updated;

}