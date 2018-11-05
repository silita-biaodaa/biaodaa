package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CollecNotice {
    private Integer id;

    private String userid;

    private String type;

    private String noticeid;

    /**
     * 收藏公告的相关公告条数
     */
    private Integer noticerelationnum;

    private String title;

    private Date collectime;

    private Integer readorreaded;

    private String source;
}