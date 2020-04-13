package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPhoneActivity;
import com.silita.biaodaa.utils.MyMapper;

public interface TbPhoneActivityMapper extends MyMapper<TbPhoneActivity> {

    /**
     * 添加
     * @param phoneActivity
     */
    void insertPhoneActivity(TbPhoneActivity phoneActivity);

}