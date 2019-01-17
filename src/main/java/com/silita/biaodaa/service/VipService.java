package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.VipInfoMapper;
import com.silita.biaodaa.model.TbVipFeeStandard;
import com.silita.biaodaa.model.TbVipProfits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by dh on 2019/1/4.
 */
@Service
public class VipService {
    @Autowired
    private VipInfoMapper vipInfoMapper;

    public List<TbVipFeeStandard> queryFeeStandard(String channel){
        return vipInfoMapper.queryFeeStandard(channel);
    }

    /**
     * 查询收益列表（分页）
     * @param page
     * @param userId
     * @return
     */
    public PageInfo queryProfitInfo(Page page, String userId){
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<TbVipProfits> list = vipInfoMapper.queryProfitInfo(userId);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 获取用户的收益天数总和
     * @param userId
     * @return
     */
    public Integer queryProfitTotal(String userId){
        return vipInfoMapper.queryProfitTotal(userId);
    }

}
