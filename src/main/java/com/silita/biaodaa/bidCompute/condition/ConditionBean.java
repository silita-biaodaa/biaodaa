package com.silita.biaodaa.bidCompute.condition;/**
 * Created by zhangxiahui on 16/9/14.
 */

import com.silita.biaodaa.bidCompute.BlockConfig;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * 条件积木基类,条件积木继承该类
 *
 * @author zhangxiahui
 * @version 1.0
 * @date 2016/09/14 上午9:53
 */
@Setter
@Getter
public class ConditionBean implements BlockConfig {

    /**
     * 条件名称,积木名称
     * */
    private String code;


    /**
     * 资源名
     * */
    private String resourceName;

    public void init(Map<String,Object> map){
        this.code = MapUtils.getString(map,"code");
        this.resourceName = MapUtils.getString(map,"resourceName");
    }
}
