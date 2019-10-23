package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbPlatformNotice;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * 平台公告
 */
public interface TbPlatformNoticeMapper extends MyMapper<TbPlatformNotice> {

    /**
     * 平台公告列表
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryPlatformNoticeList(Map<String, Object> param);

    /**
     * 根据发布时间获取详细信息
     *
     * @param param
     * @return
     */
    Map<String, Object> queryPlatformInfoByParam(Map<String,Object> param);
}