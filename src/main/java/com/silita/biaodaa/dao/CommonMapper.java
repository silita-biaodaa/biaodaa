package com.silita.biaodaa.dao;

import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/4/10.
 */
public interface CommonMapper {

    List<Map<String, Object>> queryPbMode();

    List queryCertZzByCompanyName(String companyName);

    int queryClickByUserId(Map argMap);

    void updateUserClick(Map argMap);

    void insertUserClick(Map argMap);

}
