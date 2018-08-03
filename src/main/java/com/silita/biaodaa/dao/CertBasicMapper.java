package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.CertBasic;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

/**
 * cert_basic Mapper
 */
public interface CertBasicMapper extends MyMapper<CertBasic> {

    /**
     * 获取旧公司数据
     *
     * @return
     */
    List<CertBasic> getCertBasicMap();
}