package com.silita.biaodaa.dao;

import java.util.Map;

/**
 * 版本查询
 */
public interface VersionMapper {

    /**
     * 获得版本信息
     *
     * @param loginChannel
     * @return
     */
    String getVersion(String loginChannel);

    /**
     * 获得版本信息
     * @param loginChannel
     * @return
     */
    Map<String,Object> queryVersion(String loginChannel);
}