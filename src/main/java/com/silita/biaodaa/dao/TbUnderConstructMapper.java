package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbUnderConstruct;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 *  tb_under_construct Mapper
 */
public interface TbUnderConstructMapper extends MyMapper<TbUnderConstruct> {

    /**
     * 批量添加
     * @param underConstruct
     * @return
     */
    int insertUnderConstruct(TbUnderConstruct underConstruct);

    /**
     * 分页查询
     * @param param
     * @return
     */
    List<Map<String,Object>> queryUnderConstructList(Map<String,Object> param);

    /**
     *
     * 查询信息是否存在
     * @param underConstruct
     * @return
     */
    int queryUnderCount(TbUnderConstruct underConstruct);

    /**
     * 根据innerid查询个数
     * @param innerid
     * @return
     */
    int queryUnderConstructByInnerid(String innerid);

    /**
     * 查询在建列表
     * @param innerid
     * @return
     */
    List<TbUnderConstruct> queryUnderListInnerid(String innerid);
}