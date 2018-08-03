package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbSafetyCertificate;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

/**
 * tb_safety_certificate Mapper
 */
public interface TbSafetyCertificateMapper extends MyMapper<TbSafetyCertificate> {

    /**
     * 获取安全许可证
     * @return
     */
    List<TbSafetyCertificate> getSafetyCertMap();
}