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
     * 该企业是否一周内最新数据
     * @return
     */
    int queryGsCompanyCount(Map<String, Object> param);

    /**
     * 修改
     * @param companyUpdate
     * @return
     */
    int updated(TbCompanyUpdate companyUpdate);

    /**
     * 删除公司更新信息
     * @return
     */
    int deleteCompanyUpdated(String comId);

    /**
     * 查询企业的更新状态
     * @param param
     * @return
     */
    int queryCompanyUpdatedState(Map<String,Object> param);

    /**
     * 添加
     * @param companyUpdate
     * @return
     */
    int insert(TbCompanyUpdate companyUpdate);

    /**
     * 查询点击一键更新的用户
     * @return
     */
    List<String> queryCompanyUpdatedForUsers(Map<String,Object> param);
}