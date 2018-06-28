package com.silita.biaodaa.task;

import com.silita.biaodaa.controller.vo.Page;
import com.silita.biaodaa.dao.TbPersonQualificationMapper;
import com.silita.biaodaa.service.TbCompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PersonTask implements Runnable {

    private Logger logger = LoggerFactory.getLogger(PersonTask.class);

    @Autowired
    TbCompanyService tbCompanyService;
    @Autowired
    TbPersonQualificationMapper tbPersonQualificationMapper;

    @Override
    public void run() {
        Page page = new Page();
        page.setPageSize(20);
        List<Map<String, Object>> provinceList = tbPersonQualificationMapper.getProvinceList();
        Map<String,Object> param = new HashMap<>();
        if (null != provinceList && provinceList.size() > 0) {
            for (Map<String, Object> provin : provinceList) {
                logger.info("---------缓存【" + provin.get("code") + "】省人员数据begin------------");
                param.put("tableCode",provin.get("code").toString());
                for (int i = 0; i < 5; i++) {
                    page.setCurrentPage((i+1));
                    tbCompanyService.getPersonCacheMap(page,param);
                }
                logger.info("---------缓存【" + provin.get("code") + "】省人员数据end------------");
            }
        }
    }
}
