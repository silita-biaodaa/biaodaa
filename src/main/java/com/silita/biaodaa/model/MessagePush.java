package com.silita.biaodaa.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessagePush {
    private Integer id;

    private String userid;

    private String mainid;

    private String relationid;

    private String snatchurl;

    private String title;

    private String createdate;

    /**
     * 0未发送1发送
     */
    private Integer isSend;

    /**
     * 0为系统消息1为变更消息
     */
    private Integer isSystem;

    /**
     * 类型:0招标信息，招标预告，招标变更1，中标结果2
     */
    private Integer type;

    private String message;

    private Integer readOrReaded;
}