package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbHotWords;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

/**
 * tb_hot_words Mapper
 */
public interface TbHotWordsMapper extends MyMapper<TbHotWords> {

    /**
     * 热词
     *
     * @param params
     * @return
     */
    List<TbHotWords> listHotWordsByType(Map<String, Object> params);

    /**
     * 查询热词
     * @param params
     * @return
     */
    List<String> queryHotWords(Map<String,Object> params);
}