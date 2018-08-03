package com.silita.biaodaa.dao;

import com.silita.biaodaa.controller.vo.CompanyQual;
import com.silita.biaodaa.model.AptitudeDictionary;
import com.silita.biaodaa.model.TbZzGrade;
import com.silita.biaodaa.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * tb_zz_grade Mapper
 */
public interface TbZzGradeMapper extends MyMapper<TbZzGrade> {

    /**
     * 获取资质
     *
     * @param param
     * @return
     */
    AptitudeDictionary queryApti(Map<String, Object> param);

    /**
     * 添加
     *
     * @param grade
     * @return
     */
    int insertDate(TbZzGrade grade);

    /**
     * 查询
     *
     * @param param
     * @return
     */
    TbZzGrade selectDate(Map<String, Object> param);

    /**
     * 修改
     *
     * @param pkid
     * @return
     */
    int updateDate(Integer pkid);

    /**
     * 获取资质等级
     *
     * @param zzId
     * @param type
     * @return
     */
    List<CompanyQual> quaryList(@Param("zzId") Integer zzId, @Param("type") Integer type);
}