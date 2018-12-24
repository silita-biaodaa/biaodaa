package com.silita.biaodaa.controller.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhangxiahui on 18/4/9.
 */
@Getter
@Setter
public class CompanyQual implements Serializable {

    public String name;

    public String code;

    public List<CompanyQual> list;
}
