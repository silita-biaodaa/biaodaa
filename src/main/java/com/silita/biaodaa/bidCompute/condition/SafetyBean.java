package com.silita.biaodaa.bidCompute.condition;

import com.silita.biaodaa.utils.Name;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

@Name
@Getter
@Setter
public class SafetyBean extends ConditionBean{

    /**省级优秀**/
    private Double provinceExcell;

    /**省级合格**/
    private Double provinceQual;

    /**市级优秀**/
    private Double cityExcell;

    /**市级合格**/
    private Double cityQual;

    public void init(Map<String,Object> map) {
        super.init(map);
        this.provinceExcell = MapUtils.getDouble(map,"provinceExcell");
        this.provinceQual = MapUtils.getDouble(map,"provinceQual");
        this.cityExcell = MapUtils.getDouble(map,"cityExcell");
        this.cityQual = MapUtils.getDouble(map,"cityQual");
    }
}
