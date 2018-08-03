package com.silita.biaodaa.dao;

import java.util.List;
import java.util.Map;

/**
 * zhaobiao_detail Mapper
 */
public interface ZhaobiaoProjectMapper {

    /**
     * 查询招标项目
     *
     * @param params
     * @return
     */
    public List<Map<String, Object>> queryProjects(Map params);
}