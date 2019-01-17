package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.security.Timestamp;

/**
 * 会员收费标准
 */
@Getter
@Setter
public class TbVipFeeStandard {
    private String feeStdId;
    private String stdCode;
    private double price;
    private String stdDesc;
    private String altInfo;
    private String discountId;
    private boolean state;
    private String channel;
    private String remark;
    private Timestamp updated;
    private String updateBy;
}
