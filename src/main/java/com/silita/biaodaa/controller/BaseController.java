package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.model.Page;
import com.silita.biaodaa.model.SysUser;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dh on 2018/4/11.
 */
public abstract class BaseController {
    public String CODE_FLAG = "code";

    public String MSG_FLAG = "msg";

    public String SUCCESS_CODE = Constant.SUCCESS_CODE;

    public String SUCCESS_MSG = "操作成功";

    public String FAIL_CODE = Constant.FAIL_CODE;

    public String INVALIDATE_PARAM_CODE = Constant.ERR_VIEW_CODE;

    public String INVALIDATE_PARAM_MSG = "前端参数有误";

    public int maxPageNum = 30;

    public int maxPageSize = 20;

    public String minDate = "2018-01-01";

    public String DATA_FLAG = "data";

    protected void successMsg(Map resultMap) {
        resultMap.put(this.CODE_FLAG, SUCCESS_CODE);
        resultMap.put(this.MSG_FLAG, SUCCESS_MSG);
    }

    protected Map<String,Object> successMsgs(Object obj) {
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put(this.CODE_FLAG, SUCCESS_CODE);
        resultMap.put(this.MSG_FLAG, SUCCESS_MSG);
        resultMap.put("data",obj);
        return resultMap;
    }


    protected void successMsg(Map resultMap, Object data) {
        successMsg(resultMap);
        resultMap.put("data", data);
    }

    protected Map<String,Object> responseMsg(Map resultMap, Object data) {
        successMsg(resultMap, data);
        return resultMap;
    }

    protected void errorMsg(Map resultMap, String errMsg) {
        resultMap.put(this.CODE_FLAG, this.FAIL_CODE);
        resultMap.put(this.MSG_FLAG, errMsg);
    }

    protected void errorMsg(Map resultMap, String errCode, String errMsg) {
        resultMap.put(this.CODE_FLAG, errCode);
        resultMap.put(this.MSG_FLAG, errMsg);
    }

    protected Page buildPage(Map params) {
        Integer pageNo = MapUtils.getInteger(params, "pageNo");
        Integer pageSize = MapUtils.getInteger(params, "pageSize");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageNo);
        return page;
    }


    protected void buildReturnMap(Map resultMap, PageInfo pageInfo) {
        if (pageInfo != null) {
            resultMap.put("data", pageInfo.getList());
            resultMap.put("pageNo", pageInfo.getPageNum());
            resultMap.put("pageSize", pageInfo.getPageSize());
            resultMap.put("total", pageInfo.getTotal());
            resultMap.put("pages", pageInfo.getPages());
        }
    }
    protected void buildReturnMap2(Map resultMap, PageInfo pageInfo) {
        if (pageInfo != null) {
            resultMap.put("list", pageInfo.getList());
            resultMap.put("pageNo", pageInfo.getPageNum());
            resultMap.put("pageSize", pageInfo.getPageSize());
            resultMap.put("total", pageInfo.getTotal());
            resultMap.put("pages", pageInfo.getPages());
        }
    }

    protected void buildReturnMap3(Map resultMap, PageInfo pageInfo) {
        if (pageInfo != null) {
            resultMap.put("data", pageInfo.getList());
            resultMap.put("total", pageInfo.getTotal());
            resultMap.put("pages", pageInfo.getPages());
        }
    }

    protected Page buildPageIndex(Map params) {
        Integer pageNo = MapUtils.getInteger(params, "pageNo");
        if (pageNo > maxPageNum) {
            pageNo = 1;
        }
        params.put("pageNo", pageNo);
        return buildPage(params);
    }

    protected String serQualCode(String qualCode){
        String code = "";
        if (null != qualCode) {
            String[] qualCodes = qualCode.split(",");
            String[] qual = null;
            if (qualCodes != null && qualCodes.length > 0) {
                for (String str : qualCodes) {
                    qual = MyStringUtils.splitParam(str);
                    if (null != qual && qual.length > 0) {
                        code = code + qual[qual.length - 1] + ",";
                    }
                }
            }
        }
        return code;
    }

    public static void bulidUserPwd(SysUser sysUser) {
//        if (MyStringUtils.isNotNull(sysUser.getLoginPwd())) {
//            //非app渠道的密码需要加密
//            if (!Constant.CHANNEL_ANDROID.equals(sysUser.getChannel())
//                    && !Constant.CHANNEL_IOS.equals(sysUser.getChannel())) {
//                sysUser.setLoginPwd(DigestUtils.shaHex(sysUser.getLoginPwd()));
//            }
//        }
    }
}
