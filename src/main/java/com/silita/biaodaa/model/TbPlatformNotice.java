package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
public class TbPlatformNotice {
    /**
     * 主键
     */
    private Integer pkid;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 发布时间
     */
    private String releaseTime;

    /**
     * 用于统计时间(后台不可维护)
     */
    private Date countDate;

    /**
     * 公告类型(1:平台公告，默认为1)
     */
    private Integer type;

    /**
     * 状态(当进行删除操作时status=0)
     */
    private Integer status;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 创建时间，默认当前时间
     */
    private Date cerated;

    /**
     * 修改时间
     */
    private Date updated;

    /**
     * 内容描述
     */
    private String remark;
}