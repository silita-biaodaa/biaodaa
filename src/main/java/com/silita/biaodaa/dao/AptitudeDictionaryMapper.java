package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.AptitudeDictionary;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;

public interface AptitudeDictionaryMapper extends MyMapper<AptitudeDictionary> {

    List<AptitudeDictionary> queryAptitude();
}