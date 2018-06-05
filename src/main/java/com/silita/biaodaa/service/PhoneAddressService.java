package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbPhoneAddressBookMapper;
import com.silita.biaodaa.model.TbPhoneAddressBook;
import com.silita.biaodaa.utils.JsonUtils;
import com.silita.biaodaa.utils.MyStringUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PhoneAddressService {

    @Autowired
    TbPhoneAddressBookMapper tbPhoneAddressBookMapper;

    public Map<String,Object> addPhoneAddress(String json){
        Map<String,Object> param = JsonUtils.json2Map(json);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("code",0);
        resultMap.put("msg","操作失败");
        String deviceId = null;
        //设备id
        if(null != param.get("deviceId")){
            deviceId = MapUtils.getString(param,"deviceId");
        }
        if(MyStringUtils.isNull(deviceId)){
            return resultMap;
        }
        List<Map<String,Object>> list = null;
        if(null != param.get("list")){
            list = (List<Map<String,Object>>) param.get("list");
        }
        if(null == list || list.size() == 0){
            return resultMap;
        }
        //登录手机号
        String userPhone = MapUtils.getString(param,"userPhone");
        //来源
        String sourceFrom = MapUtils.getString(param,"sourceFrom");
        int count = tbPhoneAddressBookMapper.queryPhoneAddressCount(deviceId);
        if(count > 0){
            tbPhoneAddressBookMapper.delPhoneAddress(deviceId);
        }
        List<TbPhoneAddressBook> phoneAddressBookList = new ArrayList<>();
        TbPhoneAddressBook tbPhoneAddressBook = null;
        for(Map<String,Object> tbMap : list){
            tbPhoneAddressBook = new TbPhoneAddressBook();
            tbPhoneAddressBook.setDeviceId(deviceId);
            tbPhoneAddressBook.setCreatedDate(new Date());
            tbPhoneAddressBook.setUserPhone(userPhone);
            tbPhoneAddressBook.setPhoneName(tbMap.get("phoneName").toString());
            tbPhoneAddressBook.setPhone(tbMap.get("phone").toString());
            tbPhoneAddressBook.setSourceFrom(sourceFrom);
            phoneAddressBookList.add(tbPhoneAddressBook);
        }
        if(null != phoneAddressBookList && phoneAddressBookList.size() > 0){
            tbPhoneAddressBookMapper.batchInsertPhoneAddress(phoneAddressBookList);
        }
        resultMap.put("code",1);
        resultMap.put("msg","操作成功");
        return resultMap;
    }

}
