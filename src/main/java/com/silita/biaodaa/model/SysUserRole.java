package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysUserRole {
    private String pkid;
    private String role_id;
    private String user_id;
    private String created;
    private String create_by;

    private String role_code;

}