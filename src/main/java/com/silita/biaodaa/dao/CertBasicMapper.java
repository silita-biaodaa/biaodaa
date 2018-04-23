package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.CertBasic;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface CertBasicMapper extends MyMapper<CertBasic> {

    List<CertBasic> getCertBasicMap();
}