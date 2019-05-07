package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbQuestionCase;
import com.silita.biaodaa.utils.MyMapper;

public interface TbQuestionCaseMapper extends MyMapper<TbQuestionCase> {

    int insert(TbQuestionCase questionCase);

    int queryCaseCount(String caseName);
}