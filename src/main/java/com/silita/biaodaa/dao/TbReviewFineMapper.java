package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbReviewFine;
import com.silita.biaodaa.utils.MyMapper;

import java.util.List;
import java.util.Map;

public interface TbReviewFineMapper extends MyMapper<TbReviewFine> {

    /**
     * 查询考评优良(分值计算用)
     * @param param
     * @return
     */
    List<Map<String,Object>> queryReviewFineList(Map<String,Object> param);

    /**
     * 查询企业考评优良
     * @param param
     * @return
     */
    List<Map<String,Object>> queryCompanyReviewFineList(Map<String,Object> param);
}