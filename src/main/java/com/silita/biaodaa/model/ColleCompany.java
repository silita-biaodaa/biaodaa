package com.silita.biaodaa.model;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColleCompany {

    private Integer id;

    /**
     * 公司Id
     */
    private String companyid;

    /**
     * 公司名称
     */
    private String companyname;

    /**
     * 收藏用户Id
     */
    private String userid;

    private String tablename;
}