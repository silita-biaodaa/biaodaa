package com.silita.biaodaa.bidCompute;/**
 * Created by zhangxiahui on 16/9/14.
 */

import java.util.Map;

/**
 * @author zhangxiahui
 * @version 1.0
 * @date 2016/09/14 下午6:45
 */
public interface Handler<T extends BlockConfig>{
    void init(T config);
    Map<String,Object> handlerRequest(Map<String, Object> resourceMap);
    void setSuccessor(Handler successor);
}
