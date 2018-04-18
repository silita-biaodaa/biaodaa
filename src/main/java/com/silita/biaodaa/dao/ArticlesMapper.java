package com.silita.biaodaa.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 公示文章/行业资讯
 */
public interface ArticlesMapper {
    List<Map> queryArticleList(Map params);
}
