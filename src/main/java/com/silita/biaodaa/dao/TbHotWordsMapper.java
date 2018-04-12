package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbHotWords;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbHotWordsMapper extends MyMapper<TbHotWords> {
    List<TbHotWords> listHotWordsByType(Map<String, Object> params);
}