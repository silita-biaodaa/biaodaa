package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbCompanyUpdateMapper;
import com.silita.biaodaa.model.TbCompanyUpdate;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/6/27.
 */
@Service
public class CompanyForUpdatedService {

    @Autowired
    TbCompanyUpdateMapper tbCompanyUpdateMapper;

    /**
     * 返回待更新企业
     * @return
     */
    public List<Map<String,Object>> listCompanyUpdated(){
        Map<String,Object> param = new HashedMap(){{
            put("isUpdated", 1);
            put("limit",10);
        }};
        List<TbCompanyUpdate> comList = tbCompanyUpdateMapper.queryCompanyUpdated(param);
        List<Map<String,Object>> resultList = new ArrayList<>();
        Map<String,Object> resultMap = null;
        for (TbCompanyUpdate companyUpdate : comList){
            resultMap = new HashedMap();
            resultMap.put("comId",companyUpdate.getComId());
            resultMap.put("comName",companyUpdate.getComName());
            resultList.add(resultMap);
            companyUpdate.setIsUpdated(0);
            tbCompanyUpdateMapper.updated(companyUpdate);
        }
        return resultList;
    }

    /**
     * 更新完成
     */
    public void finishCompany(Map<String,Object> param){
        //从Hbase查询数据并解析入库
        //消息通知
        //修改状态
        TbCompanyUpdate companyUpdate = new TbCompanyUpdate();
        companyUpdate.setComId(MapUtils.getString(param,"comId"));
        companyUpdate.setIsUpdated(2);
        tbCompanyUpdateMapper.updatedStatus(companyUpdate);
    }
}
