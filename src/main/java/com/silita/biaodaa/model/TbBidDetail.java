package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
public class TbBidDetail {

    private Integer pkid;

    /**
     * 评标预测结果id
     */
    private Integer forePkid;

    /**
     * 计分规则各奖项和不良行为和安全认证关联id
     */
    private String certId;

    /**
     * 类型;1:获奖情况,2:不良行为,3:企业认证
     */
    private Integer certType;

    /**
     * 创建时间
     */
    private Date created;
}