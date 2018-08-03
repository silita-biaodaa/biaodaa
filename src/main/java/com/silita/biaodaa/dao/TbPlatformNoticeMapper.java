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
     * 获取月的平台公告数
     *
     * @param statDate
     * @return
     */
    int queryPlatformNoticeCount(String statDate);

    /**
     * 平台公告列表
     *
     * @param param
     * @return
     */
    List<Map<String, Object>> queryPlatformNoticeList(Map<String, Object> param);

    /**
     * 修改公告内容
     *
     * @param param
     * @return
     */
    int updateRemark(Map<String, Object> param);

    /**
     * 添加
     *
     * @param notice
     * @return
     */
    int insertPlatformNotice(TbPlatformNotice notice);

    /**
     * 根据发布时间获取详细信息
     *
     * @param statDate
     * @return
     */
    Map<String, Object> queryPlatformInfoByStatDate(String statDate);
}