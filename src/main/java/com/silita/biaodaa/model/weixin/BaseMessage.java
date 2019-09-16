package com.silita.biaodaa.model.weixin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by zhangxiahui on 17/6/14.
 */
@Setter
@Getter
public class BaseMessage implements Serializable {
    private String ToUserName;
    private String FromUserName;
    private Long CreateTime;
    private String MsgType;
    private Long MsgId;
}