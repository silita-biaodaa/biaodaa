package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbTrafficPerson;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface TbTrafficPersonMapper extends MyMapper<TbTrafficPerson> {

    /**
     * 根据项目id查询人员信息
     * @param proId
     * @return
     */
    List<TbTrafficPerson> queryPersonListByProId(String proId);

}