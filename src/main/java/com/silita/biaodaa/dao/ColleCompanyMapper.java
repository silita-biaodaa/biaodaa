package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.ColleCompany;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * colle_company_new Mapper
 */
public interface ColleCompanyMapper extends MyMapper<ColleCompany> {

    /**
     * 获取公司关注详请
     * @param colleCompany
     */
    ColleCompany getCollectionCompanyByUserIdAndCompanyId(ColleCompany colleCompany);

    /**
     *添加
     * @param colleCompany
     */
    void insertCollectionCompany(ColleCompany colleCompany);

    /**
     *删除
     * @param colleCompany
     */
    void deleteCollectionCompany(ColleCompany colleCompany);

    /**
     *获取湖南关注企业
     * @param params
     * @return
     */
    List<Map<String, Object>> listHuNanCollectionCompany(Map<String, Object> params);

    /**
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> listNationWideCollectionCompany(Map<String, Object> params);

    /**
     *获取关注企业
     * @param params
     * @return
     */
    List<Map<String, Object>> listCollectionCompany(Map<String, Object> params);
}