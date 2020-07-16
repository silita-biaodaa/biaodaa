package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.CertUndesirable;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * cert_undesirable Mapper
 */
public interface CertUndesirableMapper extends MyMapper<CertUndesirable> {

    /**
     * 获取不良记录
     *
     * @param param
     * @return
     */
    List<CertUndesirable> queryCertUndesinList(Map<String, Object> param);

    void insertCertUndesirable(CertUndesirable cert);
}