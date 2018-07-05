package com.silita.biaodaa.bidCompute.condition;

import com.silita.biaodaa.utils.Name;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

/**
 * 获奖bean
 * created by zhushuai
 */
@Name
@Getter
@Setter
public class PrizeBean extends ConditionBean{

    /**国家超过多少项不计分**/
    private Integer nationPrizeCon;

    /**鲁班奖分值**/
    private Double lubanPrize;

    /**全国建设工程项目施工安全生产标准化工地分值**/
    private Double buildPrize;

    /**全国建筑工程装饰奖分值**/
    private Double decoratePrize;

    /**省级超过多少项**/
    private Integer provinceCount;

    /**超过多少按百分比计分**/
    private Double overPercent;

    /**芙蓉奖分值**/
    private Double lotusPrize;

    /**省优质工程分值**/
    private Double superPrize;

    public void init(Map<String,Object> map){
        super.init(map);
        this.nationPrizeCon = MapUtils.getInteger(map,"nationPrizeCon");
        this.lubanPrize = MapUtils.getDouble(map,"lubanPrize");
        this.buildPrize = MapUtils.getDouble(map,"buildPrize");
        this.decoratePrize = MapUtils.getDouble(map,"decoratePrize");
        this.provinceCount = MapUtils.getInteger(map,"provinceCount");
        this.overPercent = MapUtils.getDouble(map,"overPercent");
        this.lotusPrize = MapUtils.getDouble(map,"lotusPrize");
        this.superPrize = MapUtils.getDouble(map,"superPrize");
    }
}
