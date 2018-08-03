package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.SnatchurlEs;

import java.util.List;
import java.util.Map;

/**
 * 公告主表
 */
public interface SnatchurlMapper {

    /**
     * 获取公告主项目信息
     *
     * @param param
     * @return
     */
    List<SnatchurlEs> querySnatchurlList(Map<String, Object> param);

    /**
     * 获取公告详情信息
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryNoticeDetailBySnatchurlId(Map<String, Object> param);

    /**
     * 获取公告主项目条数
     *
     * @return
     */
    Integer querySnatchurlCount();
}