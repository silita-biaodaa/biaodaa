package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbDuanwuActivity;
import com.silita.biaodaa.utils.MyMapper;

public interface TbDuanwuActivityMapper extends MyMapper<TbDuanwuActivity> {

    /**
     * 添加
     * @param duanwuActivity
     * @return
     */
    int insert(TbDuanwuActivity duanwuActivity);

    /**
     * 修改支付状态
     * @param tbDuanwuActivity
     * @return
     */
    int updatePayState(TbDuanwuActivity tbDuanwuActivity);

    /**
     * 修改
     * @param tbDuanwuActivity
     * @return
     */
    int update(TbDuanwuActivity tbDuanwuActivity);

    /**
     * 查询订单号是否存在
     * @param orderNo
     * @return
     */
    int queryOrderNoExist(String orderNo);
}