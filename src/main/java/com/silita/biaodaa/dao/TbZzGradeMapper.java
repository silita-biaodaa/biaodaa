package com.silita.biaodaa.dao;

import com.silita.biaodaa.controller.vo.CompanyQual;
import com.silita.biaodaa.model.AptitudeDictionary;
import com.silita.biaodaa.model.TbZzGrade;
import com.silita.biaodaa.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TbZzGradeMapper extends MyMapper<TbZzGrade> {

    AptitudeDictionary queryApti(Map<String,Object> param);

    int insertDate(TbZzGrade grade);

    TbZzGrade selectDate(Map<String,Object> param);

    int updateDate(Integer pkid);

    List<CompanyQual> quaryList(@Param("zzId") Integer zzId,@Param("type") Integer type);
}