package com.silita.biaodaa.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.silita.biaodaa.common.Constant;
import com.silita.biaodaa.dao.PrizeMapper;
import com.silita.biaodaa.model.Page;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by zhushuai on 2019/3/5.
 */
@Service
public class PrizeService {

    @Autowired
    PrizeMapper prizeMapper;

    /**
     * 查询获奖查询条件
     *
     * @return
     */
    public Map<String, Object> getPrizeFilter() {
        Map<String, Object> resultMap = new HashedMap();
        Map<String, Object> param = new HashedMap() {{
            put("type", Constant.PRIZE_TYPE_COUNTRY);
        }};
        List<Map<String, Object>> countryPrizeList = prizeMapper.queryPrizeFilter(param);
        resultMap.put("country", countryPrizeList);
        param.put("type", Constant.PRIZE_TYPE_PROVINCE);
        List<Map<String, Object>> provincePrizeList = prizeMapper.queryPrizeFilter(param);
        resultMap.put("province", provincePrizeList);
        return resultMap;
    }

    /**
     * 获奖信息列表
     *
     * @param page
     * @param param
     * @return
     */
    public PageInfo getPrizeList(Page page, Map<String, Object> param) {
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String, Object>> list = prizeMapper.queryPrizeList(param);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }

    /**
     * 不良记录列表
     *
     * @param page
     * @param param
     * @return
     */
    public PageInfo getUndsirableList(Page page, Map<String, Object> param) {
        PageHelper.startPage(page.getCurrentPage(), page.getPageSize());
        List<Map<String, Object>> list = prizeMapper.queryundesirableList(param);
        PageInfo pageInfo = new PageInfo(list);
        return pageInfo;
    }
}
