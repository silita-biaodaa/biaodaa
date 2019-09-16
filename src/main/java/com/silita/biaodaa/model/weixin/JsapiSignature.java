package com.silita.biaodaa.model.weixin;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhangxiahui on 17/6/18.
 */
@Getter
@Setter
public class JsapiSignature {

    private String noncestr;

    private String timestamp;

    private String url;

    private String jsapiTicket;

    private String signature;

}
