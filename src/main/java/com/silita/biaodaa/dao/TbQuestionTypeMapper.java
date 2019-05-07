package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbQuestionType;
import com.silita.biaodaa.utils.MyMapper;

public interface TbQuestionTypeMapper extends MyMapper<TbQuestionType> {

    int insert(TbQuestionType questionType);
}