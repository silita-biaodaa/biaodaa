package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.AptitudeDictionary;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface AptitudeDictionaryMapper extends MyMapper<AptitudeDictionary> {

    List<AptitudeDictionary> queryAptitude();

    List<Map<String,String>> getIndustry();
}