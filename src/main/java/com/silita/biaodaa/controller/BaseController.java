package com.silita.biaodaa.controller;

import java.util.Map;

/**
 * Created by dh on 2018/4/11.
 */
public abstract class BaseController {
    public String CODE_FLAG="code";

    public String MSG_FLAG="msg";

    public String SUCCESS_CODE="1";

    public String SUCCESS_MSG="操作成功";

    public String FAIL_CODE="0";

    public String INVALIDATE_PARAM_CODE="9999";

    public String INVALIDATE_PARAM_MSG="前端参数有误";

    public int maxPageNum=30;

    public int maxPageSize=20;

    public String minDate="2018-01-01";

    protected void successMsg(Map resultMap){
        resultMap.put(this.CODE_FLAG, SUCCESS_CODE);
        resultMap.put(this.MSG_FLAG, SUCCESS_MSG);
    }

    protected void errorMsg(Map resultMap,String errMsg){
        resultMap.put(this.CODE_FLAG,this.FAIL_CODE);
        resultMap.put(this.MSG_FLAG,errMsg);
    }

}
