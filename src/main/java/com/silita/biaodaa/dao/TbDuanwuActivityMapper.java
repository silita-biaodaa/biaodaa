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
}