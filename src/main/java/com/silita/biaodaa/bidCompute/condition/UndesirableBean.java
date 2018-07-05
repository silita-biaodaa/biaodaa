package com.silita.biaodaa.bidCompute.condition;

import com.silita.biaodaa.utils.Name;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

@Name
@Getter
@Setter
public class UndesirableBean extends ConditionBean {

    /**一般不良行为分值**/
    private Double commScore;

    /**严重不良行为分值**/
    private Double sevScore;

    public void init(Map<String,Object> map) {
        super.init(map);
        this.commScore = MapUtils.getDouble(map,"commScore");
        this.sevScore = MapUtils.getDouble(map,"sevScore");
    }
}
