package com.silita.biaodaa.bidCompute.condition;

import com.silita.biaodaa.utils.Name;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * Created by zhangxiahui on 18/7/3.
 */
@Name
@Getter
@Setter
public class ProjectBean extends ConditionBean {


    /**
     * 超过多少项不计分
     * */
    private Integer overflow;

    /**
     * 每项省内工程分数
     * */
    private Integer provinceInner;

    /**
     * 每项省外工程分数
     * */
    private Integer provinceOuter;

    public void init(Map<String,Object> map){
        super.init(map);
        this.overflow = MapUtils.getInteger(map,"overflow");
        this.provinceInner = MapUtils.getInteger(map,"provinceInner");
        this.provinceOuter = MapUtils.getInteger(map,"provinceOuter");
    }




}
