package com.silita.biaodaa.dao;

import java.util.Map;

public interface TbPersonMapper {
    /**
     * 获得人员详细信息
     * @param params
     * @return
     */
    public Map<String, Object> queryPersonDetail(Map<String, Object> params);
}