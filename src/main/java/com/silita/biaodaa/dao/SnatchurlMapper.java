package com.silita.biaodaa.dao;

import java.util.List;
import java.util.Map;

/**
 * 公告主表
 */
public interface SnatchurlMapper{

    /**
     * 获取公告主项目信息
     * @param param
     * @return
     */
    List<Map<String,Object>> querySnatchurlList(Map<String,Object> param);

    /**
     * 获取公告详情信息
     * @param param
     * @return
     */
    List<Map<String,Object>> queryNoticeDetailBySnatchurlId(Map<String,Object> param);
}