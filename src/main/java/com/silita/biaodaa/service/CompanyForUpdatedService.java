package com.silita.biaodaa.service;

import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.common.VisitInfoHolder;
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
    @Autowired
    CompanyHbaseService companyHbaseService;
    @Autowired
    MessageService messageService;

    /**
     * 返回待更新企业
     *
     * @return
     */
    public List<Map<String, Object>> listCompanyUpdated() {
        Map<String, Object> param = new HashedMap() {{
            put("isUpdated", 1);
            put("limit", 1);
        }};
        List<TbCompanyUpdate> comList = tbCompanyUpdateMapper.queryCompanyUpdated(param);
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> resultMap = null;
        for (TbCompanyUpdate companyUpdate : comList) {
            resultMap = new HashedMap();
            resultMap.put("comId", companyUpdate.getComId());
            resultMap.put("comName", companyUpdate.getComName());
            resultList.add(resultMap);
            companyUpdate.setIsUpdated(0);
            String[] pkids = companyUpdate.getPkid().split(",");
            for (String id : pkids) {
                companyUpdate.setPkid(id);
                tbCompanyUpdateMapper.updated(companyUpdate);
            }
        }
        comList = null;
        return resultList;
    }

    /**
     * 更新完成
     */
    public void finishCompany(Map<String, Object> param) {
        //消息通知
        List<String> users = tbCompanyUpdateMapper.queryCompanyUpdatedForUsers(param);
        messageService.pushMessage(param, users);
        users = null;
        //修改更新记录
        tbCompanyUpdateMapper.deleteCompanyUpdated(MapUtils.getString(param, "comId"));
    }

    /**
     * 一键更新
     *
     * @param param
     * @return
     */
    public Map<String, Object> updatedCompany(Map<String, Object> param) {
        Map<String, Object> resultMap = new HashedMap();
        resultMap.put("code", Constant.WARN_CODE_405);
        //该企业是否一周内最新数据
        if (tbCompanyUpdateMapper.queryGsCompanyCount(param) > 0) {
            resultMap.put("msg", "企业的【工商数据】为最新信息");
            return resultMap;
        }
        //该企业在待更新
        if (tbCompanyUpdateMapper.queryCompanyUpdatedState(param) > 0) {
            resultMap.put("msg", "正在更新中。。。");
            //添加数据
            saveCompanyUpdate(param);
            return resultMap;
        }
        //添加数据
        saveCompanyUpdate(param);
        resultMap.put("code", 1);
        resultMap.put("msg", "企业的【工商数据】将更新至最新信息，更新完成后，会有消息通知到您！！");
        return resultMap;
    }

    private void saveCompanyUpdate(Map<String, Object> param) {
        param.put("userId", VisitInfoHolder.getUid());
        if (tbCompanyUpdateMapper.queryCompanyUserUpdate(param) > 0) {
            return;
        }
        //添加数据
        TbCompanyUpdate companyUpdate = new TbCompanyUpdate();
        companyUpdate.setComId(MapUtils.getString(param, "comId"));
        companyUpdate.setComName(MapUtils.getString(param, "comName"));
        companyUpdate.setUserId(VisitInfoHolder.getUid());
        companyUpdate.setIsUpdated(1);
        tbCompanyUpdateMapper.insert(companyUpdate);
        companyUpdate = null;
    }
}
