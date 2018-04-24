package com.silita.biaodaa.dao;

import java.util.List;
import java.util.Map;

/**
 * 第三方链接
 */
public interface BlogrollMapper {
    /**
     * 筛选第三方链接
     * @param params
     */
    public List<Map> queryLinks(Map params);
}
