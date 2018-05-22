package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by 91567 on 2018/5/22.
 */
@Getter
@Setter
public class ClickRecord {

    private Integer id;

    /**
     * 访问的文章类型。article，文章公示；neirong，招标公告中标公告；fileneirong，文件通知文件启示；zizhi，企业信息',
     */
    private String type;

    /**
     * 访问的文章Id
     */
    private Integer aimId;

    /**
     * 访问次数
     */
    private Integer visitTimes;

    /**
     * 最后一次访问时间
     */
    private String lastVisitTime;

    /**
     * 日期
     */
    private String date;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 用户id
     */
    private String userId;
}
