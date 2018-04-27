package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.ColleCompany;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface ColleCompanyMapper extends MyMapper<ColleCompany> {

    /**
     *
     * @param colleCompany
     */
    ColleCompany getCollectionCompanyByUserIdAndCompanyId(ColleCompany colleCompany);

    /**
     *
     * @param colleCompany
     */
    void insertCollectionCompany(ColleCompany colleCompany);

    /**
     *
     * @param colleCompany
     */
    void deleteCollectionCompany(ColleCompany colleCompany);

    /**
     *
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
     *
     * @param params
     * @return
     */
    List<Map<String, Object>> listCollectionCompany(Map<String, Object> params);
}