package com.silita.biaodaa.dao;

import java.util.List;
import java.util.Map;

/**
 * 常用链接
 */
public interface CustomUrlMapper {
    /**
     * 筛选常用链接
     * @param params
     * @return
     */
     List<Map> queryCustomUrls(Map params);
}
