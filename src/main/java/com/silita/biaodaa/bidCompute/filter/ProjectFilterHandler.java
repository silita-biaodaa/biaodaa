package com.silita.biaodaa.bidCompute.filter;

import com.silita.biaodaa.bidCompute.condition.ProjectBean;
import com.silita.biaodaa.utils.Name;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by zhangxiahui on 18/7/3.
 */
@Name
public class ProjectFilterHandler extends BaseFilterHandler<ProjectBean> {

    private static final Logger logger = LoggerFactory.getLogger(ProjectFilterHandler.class);

    @Override
    String getFilterName() {
        return "类似项目业绩";
    }

    @Override
    Double doHandler(Map resourceMap) {
        // TODO: 18/7/3 处理资源数据


        return 1d;
    }
}
