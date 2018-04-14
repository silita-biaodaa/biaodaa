package com.silita.biaodaa.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/4/9.
 */
public interface NoticeMapper {
    Long queryNoticesByCompanyName(String companyName);

    List queryNoticeList(Map argMap);

    String queryNoticeZZById(Long id);

    List<Map> queryComByZZ(Collection zzList);

    List<Map> queryNoticeDetail(Map map);

    Long queryRelCount(Long id);

    List<Map> queryNoticeFile(Long id);

    List<Map> queryRelations(Long id);
}
