package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by dh on 2019/1/4.
 */
@Getter
@Setter
public class VipInfo {
    private String v_id;
    private String user_id;
    private String invite_code;
    private String level;
    private String remark;
    private String permissions;
    private String role_code;
    private String expired_date;
    private String created;
    private String create_by;
    private String updated;
    private String update_by;
}
