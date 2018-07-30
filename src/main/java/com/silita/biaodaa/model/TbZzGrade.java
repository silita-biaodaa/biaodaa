package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
public class TbZzGrade {
    /**
     * 主键id
     */
    private Integer pkid;

    /**
     * 等级名称
     */
    private String gradeName;

    /**
     * 等级id
     */
    private String gradeUuid;

    /**
     * 资质id
     */
    private Integer zzId;

    /**
     * 等级类型(0：公共；1：公告；2：公司)
     */
    private Integer gradeType;

    /**
     * 创建时间
     */
    private Date created;
}