package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPhoneAddressBook;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

/**
 * 手机通讯录记录
 */
public interface TbPhoneAddressBookMapper extends MyMapper<TbPhoneAddressBook> {

    /**
     * 获取设备下手机通讯录条数
     * @param deviceId
     * @return
     */
    int queryPhoneAddressCount(String deviceId);

    /**
     * 批量插入手机通讯录
     * @param list
     * @return
     */
    int batchInsertPhoneAddress(List<TbPhoneAddressBook> list);

    /**
     * 删除记录
     * @param deviceId
     * @return
     */
    int delPhoneAddress(String deviceId);
}