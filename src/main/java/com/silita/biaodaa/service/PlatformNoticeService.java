package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.dao.TbPlatformNoticeMapper;
import com.silita.biaodaa.model.Page;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PlatformNoticeService {

    @Autowired
    TbPlatformNoticeMapper tbPlatformNoticeMapper;

    public Map<String,Object> getPlatformNoticeList(Map<String,Object> param,Map<String,Object> result){
        Integer pageNum = MapUtils.getInteger(param,"pageNo");
        Integer pageSize = MapUtils.getInteger(param,"pageSize");
        Page page = new Page();
        page.setPageSize(pageSize);
        page.setCurrentPage(pageNum);

        PageHelper.startPage(page.getCurrentPage(),page.getPageSize());
        List<Map<String,Object>> platNoticeList = tbPlatformNoticeMapper.queryPlatformNoticeList(param);
        PageInfo pageInfo = new PageInfo(platNoticeList);
        result.put("data",pageInfo.getList());
        result.put("pageNum",pageInfo.getPageNum());
        result.put("pageSize", pageInfo.getPageSize());
        result.put("total",pageInfo.getTotal());
        result.put("pages",pageInfo.getPages());
        return result;
    }
}
