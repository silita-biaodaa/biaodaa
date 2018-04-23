package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbSafetyCertificate;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbSafetyCertificateMapper extends MyMapper<TbSafetyCertificate> {

    List<TbSafetyCertificate> getSafetyCertMap();
}