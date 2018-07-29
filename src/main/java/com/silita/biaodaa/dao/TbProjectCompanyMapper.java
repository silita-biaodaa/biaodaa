package com.silita.biaodaa.dao;

import com.silita.biaodaa.model.TbProjectCompany;
import com.silita.biaodaa.utils.MyMapper;
import java.util.List;
import java.util.Map;

public interface TbProjectCompanyMapper extends MyMapper<TbProjectCompany> {

    List<String> queryProIdForCom(Map<String,Object> param);

}