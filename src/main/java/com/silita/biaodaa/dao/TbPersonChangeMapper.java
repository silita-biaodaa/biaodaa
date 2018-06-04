package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPersonChange;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

/**
 * 人员变更表
 */
public interface TbPersonChangeMapper extends MyMapper<TbPersonChange> {

    /**
     * 获取人员变更记录
     * @param flag
     * @return
     */
    List<TbPersonChange> queryPersonChangeByFlag(String flag);
}