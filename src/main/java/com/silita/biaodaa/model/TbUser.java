package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhangxiahui on 17/7/27.
 */
@Getter
@Setter
public class TbUser {

    private Integer pkid;
    private String name;// '用户名',
    private String password;// '密码',
    private String displayName;// '展示名',
    private String perId;// '人员ID',
    private String xtoken;
    private String newPassword;

}
