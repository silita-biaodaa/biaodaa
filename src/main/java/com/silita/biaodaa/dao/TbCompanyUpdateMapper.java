package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbCompanyUpdate;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbCompanyUpdateMapper extends MyMapper<TbCompanyUpdate> {

    /**
     * 查询待更新的企业信息
     * @param param
     * @return
     */
    List<TbCompanyUpdate> queryCompanyUpdated(Map<String,Object> param);

    /**
     * 修改
     * @param companyUpdate
     * @return
     */
    int updated(TbCompanyUpdate companyUpdate);

    /**
     * 更新状态
     * @return
     */
    int updatedStatus(TbCompanyUpdate companyUpdate);
}