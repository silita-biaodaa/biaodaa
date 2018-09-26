package com.silita.biaodaa.task;

import com.silita.biaodaa.common.MyRedisTemplate;
import com.silita.biaodaa.common.RedisConstantInterface;
import com.silita.biaodaa.dao.TbPersonQualificationMapper;
import com.silita.biaodaa.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ProjectTask implements Runnable{

    private Logger logger = LoggerFactory.getLogger(ProjectTask.class);

    @Autowired
    TbPersonQualificationMapper tbPersonQualificationMapper;
    @Autowired
    ProjectService projectService;
    @Autowired
    MyRedisTemplate myRedisTemplate;

    @Override
    public void run() {
        myRedisTemplate.batchDel(RedisConstantInterface.PROJECT_LIST);
        List<Map<String, Object>> provinceList = tbPersonQualificationMapper.getProvinceList();
        Map<String,Object> param = new HashMap<>();
        param.put("pageSize",20);
        if (null != provinceList && provinceList.size() > 0) {
            for (Map<String, Object> provin : provinceList) {
                logger.info("---------缓存【" + provin.get("name") + "】省业绩数据begin------------");
                param.put("area",provin.get("name").toString());
                for (int i = 0; i < 6; i++) {
                    param.put("pageNo",(i+1));
                    projectService.getProjectList(param,null);
                }
                logger.info("---------缓存【" + provin.get("name") + "】省业绩数据end------------");
            }
        }
    }
}
