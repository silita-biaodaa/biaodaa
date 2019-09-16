package com.silita.biaodaa.model.weixin;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by zhangxiahui on 17/6/14.
 */
@Setter
@Getter
public class Articles implements Serializable {
    /**
     * 图文消息标题
     */
    private String Title;
    /**
     * 图文消息描述
     */
    private String Description;
    /**
     * 图片链接，支持JPG、PNG格式，较好的效果为大图360*200，小图200*200
     */
    private String PicUrl;
    /**
     * 点击图文消息跳转链接
     */
    private String Url;
}