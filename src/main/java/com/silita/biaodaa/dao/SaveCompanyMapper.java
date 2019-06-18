package com.silita.biaodaa.dao;

import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/6/17.
 */
public interface SaveCompanyMapper extends MyMapper {

    int save(Map<String,Object> param);

    int query(Map<String,Object> param);

    int update(Map<String,Object> param);

    List<Map<String,Object>> queryList();
}
