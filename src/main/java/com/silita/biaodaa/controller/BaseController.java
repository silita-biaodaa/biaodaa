package com.silita.biaodaa.controller;

import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import org.apache.commons.collections.MapUtils;

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

    public String DATA_FLAG="data";

    protected void successMsg(Map resultMap){
        resultMap.put(this.CODE_FLAG, SUCCESS_CODE);
        resultMap.put(this.MSG_FLAG, SUCCESS_MSG);
    }

    protected void errorMsg(Map resultMap,String errMsg){
        resultMap.put(this.CODE_FLAG,this.FAIL_CODE);
        resultMap.put(this.MSG_FLAG,errMsg);
    }

    protected Page buildPage(Map params){
        Integer pageNo = MapUtils.getInteger(params, "pageNo");
        Integer pageSize = MapUtils.getInteger(params, "pageSize");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageNo);
        return page;
    }

    protected void buildReturnMap(Map resultMap,PageInfo pageInfo){
        if(pageInfo !=null) {
            resultMap.put("data", pageInfo.getList());
            resultMap.put("pageNo", pageInfo.getPageNum());
            resultMap.put("pageSize", pageInfo.getPageSize());
            resultMap.put("total", pageInfo.getTotal());
            resultMap.put("pages", pageInfo.getPages());
        }
    }

    protected void buildPageIndex(Map params){
        Integer pageNo = MapUtils.getInteger(params, "pageNo");
        if(pageNo > maxPageNum){
            pageNo = 1;
        }
        params.put("pageNo",pageNo);
    }
}
