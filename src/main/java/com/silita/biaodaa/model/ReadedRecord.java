package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReadedRecord {
    private Integer id;

    private Integer msgId;

    private String userid;

    /**
     * 0未读、1已读、2全部
     */
    private Integer readorreaded;

}