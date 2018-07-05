package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import javax.persistence.*;

@Getter
@Setter
public class TbBidResult {
    /**
     * 主键id
     */
    private Integer pkid;

    /**
     * 评标办法项目id
     */
    private Integer bidPkid;

    private String comName;

    /**
     * 投标报价
     */
    private Double bidPrice;

    /**
     * 投标报价下浮率
     */
    private String bidRate;

    /**
     * 报价得分
     */
    private Double offerScore;

    /**
     * 信誉得分
     */
    private Double creditScore;

    /**
     * 总得分
     */
    private Double total;

    /**
     * 评标状态;1:有效标;0:废标
     */
    private Integer bidStatus;

    private Date created;
}