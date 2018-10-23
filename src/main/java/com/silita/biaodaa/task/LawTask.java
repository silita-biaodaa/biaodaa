package com.silita.biaodaa.task;

import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.dao.TbCompanyMapper;
import com.silita.biaodaa.es.ElasticseachService;
import com.silita.biaodaa.model.es.CompanyEs;
import com.silita.biaodaa.model.es.CompanyLawEs;
import com.silita.biaodaa.service.LawService;
import com.silita.biaodaa.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.silita.biaodaa.common.RedisConstantInterface.LAW_LIST_OVER_TIME;

/**
 * 法务
 */
@Component
public class LawTask implements Runnable {

    @Autowired
    LawService lawService;

    @Override
    public void run() {
        lawService.countNoticCompanyLaw(null);
    }
}
