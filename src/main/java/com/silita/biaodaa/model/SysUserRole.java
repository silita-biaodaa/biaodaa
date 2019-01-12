package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserRole {
    private String pkid;
    private String roleId;
    private String userId;
    private String created;
    private String createBy;

    private String roleCode;

}