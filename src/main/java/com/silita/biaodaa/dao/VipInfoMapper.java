package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbVipFeeStandard;
import com.silita.biaodaa.model.TbVipInfo;
import com.silita.biaodaa.model.TbVipProfitSettings;
import com.silita.biaodaa.model.TbVipProfits;

import java.util.List;

public interface VipInfoMapper {
    TbVipInfo queryVipInfoById(String userId);

    int updateVipInfo(TbVipInfo tbVipInfo);

    int insertVipInfo(TbVipInfo tbVipInfo);

    List<TbVipFeeStandard>  queryFeeStandard(String channel);

    List<TbVipProfits>  queryProfitInfo(String userId);

    Integer queryProfitTotal(String userId);

    /**
     * 查询收费标准对象
     * @param stdCode
     * @return
     */
    TbVipFeeStandard queryFeeStandardByCode(String stdCode);

    /**
     * 获取活动权益
     * @param channel
     * @return
     */
    TbVipProfitSettings queryProfitSettingsByCode(String channel,String sCode);

    /**
     * 记录会员收益明细
     * @param tbVipProfits
     * @return
     */
    int insertVipProfits(TbVipProfits tbVipProfits);

    /**
     * 查询用户某活动的赠送次数
     * @param sCode
     * @param userId
     * @return
     */
    Integer queryUserProfitCount(String sCode,String userId);

}