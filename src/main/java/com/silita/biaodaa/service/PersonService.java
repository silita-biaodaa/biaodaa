package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbPersonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 人员信息Service
 * zhushuai
 */
@Service
public class PersonService {

    @Autowired
    TbPersonMapper tbPersonMapper;

    /**
     * 获取人员详情
     * created by zhushuai
     * @param perId 人员主键Id
     * @param tabType table类型
     * @return
     */
    public Map<String,Object> getPersonDetail(Integer perId, String tabType){
        return null;
    }

    /**
     * 获取注册证书，个人业绩，获奖信息，不良行为，变更记录
     * created by zhushuai
     * @param perId 人员主键Id
     * @param tabType table类型
     * @return
     */
    public List<Map<String,Object>> getPersonMajor(Integer perId, String tabType){
        return null;
    }
}
