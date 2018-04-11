package com.silita.biaodaa.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/4/9.
 */
public interface NoticeMapper {
    List queryNoticesByCompanyName(String companyName);

    List queryNoticeList(Map argMap);
}
