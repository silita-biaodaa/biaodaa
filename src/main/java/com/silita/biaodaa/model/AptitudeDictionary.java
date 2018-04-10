package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AptitudeDictionary {

    private Integer id;

    /**
     * 类型名称
     */
    private String aptitudename;

    /**
     * 类型代码
     */
    private String aptitudecode;

    /**
     * 资质名称
     */
    private String majorname;

    /**
     * 资质uuid
     */
    private String majoruuid;

    /**
     * 等级类型用：1=一二三级，2=甲乙丙级
     */
    private String zztype;
}