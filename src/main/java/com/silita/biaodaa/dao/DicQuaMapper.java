package com.silita.biaodaa.dao;

import java.util.Map;

/**
 * 资质
 */
public interface DicQuaMapper{

    /**
     * 根据资质和等级code查询关联id
     * @param param
     * @return
     */
    String queryQualGradeId(Map<String,Object> param);

}