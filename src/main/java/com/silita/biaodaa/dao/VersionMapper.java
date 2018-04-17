package com.silita.biaodaa.dao;

/**
 * 版本查询
 */
public interface VersionMapper {
    /**
     * 获得版本信息
     * @param loginChannel
     * @return
     */
    public String getVersion(String loginChannel);
}