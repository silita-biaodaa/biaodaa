package com.silita.biaodaa.dao;

import java.util.List;
import java.util.Map;

/**
 * 公示文章/行业资讯
 */
public interface ArticlesMapper {
    /**
     * 查询列表
     * @param params
     * @return
     */
    List<Map<String,Object>> queryArticleList(Map params);

    /**
     * 查询详细
     * @param id
     * @return
     */
    Map queryArticleDetail(Integer id);
}
