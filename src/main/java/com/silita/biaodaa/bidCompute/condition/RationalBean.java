package com.silita.biaodaa.bidCompute.condition;

import com.silita.biaodaa.utils.Name;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.MapUtils;

import java.util.Map;

@Name
@Getter
@Setter
public class RationalBean extends ConditionBean {

    /**
     * 最新年份
     **/
    private String year;

    /**
     * 最新年份AA级分值
     **/
    private Double yearAAScore;

    /**
     * 最新年份A级分值
     **/
    private Double yearAScore;

    /**
     * 最新年份B级分值
     **/
    private Double yearBScore;

    /**
     * 最新年份C级分值
     **/
    private Double yearCScore;

    /**
     * 前一年年份
     **/
    private String lastOneYear;

    /**
     * 前一年AA级分值
     **/
    private Double oneYearAAScore;

    /**
     * 前一年A级分值
     **/
    private Double oneYearAScore;

    /**
     * 前一年B级分值
     **/
    private Double oneYearBScore;

    /**
     * 前一年C级分值
     **/
    private Double oneYearCScore;

    /**
     * 前两年年份
     **/
    private String lastTwoYear;

    /**
     * 前两年AA级分值
     **/
    private Double twoYearAAScore;

    /**
     * 前两年A级分值
     **/
    private Double twoYearAScore;

    /**
     * 前两年B级分值
     **/
    private Double twoYearBScore;

    /**
     * 前两年C级分值
     **/
    private Double twoYearCScore;

    public void init(Map<String, Object> map) {
        super.init(map);
        this.year = MapUtils.getString(map, "year");
        this.yearAAScore = MapUtils.getDouble(map, "yearAAScore");
        this.yearAScore = MapUtils.getDouble(map, "yearAScore");
        this.yearBScore = MapUtils.getDouble(map, "yearBScore");
        this.yearCScore = MapUtils.getDouble(map, "yearCScore");
        this.lastOneYear = MapUtils.getString(map, "lastOneYear");
        this.oneYearAAScore = MapUtils.getDouble(map, "oneYearAAScore");
        this.oneYearAScore = MapUtils.getDouble(map, "oneYearAScore");
        this.oneYearBScore = MapUtils.getDouble(map, "oneYearBScore");
        this.oneYearCScore = MapUtils.getDouble(map, "oneYearCScore");
        this.lastTwoYear = MapUtils.getString(map, "lastTwoYear");
        this.twoYearAAScore = MapUtils.getDouble(map, "twoYearAAScore");
        this.twoYearAScore = MapUtils.getDouble(map, "twoYearAScore");
        this.twoYearBScore = MapUtils.getDouble(map, "twoYearBScore");
        this.twoYearCScore = MapUtils.getDouble(map, "twoYearCScore");
    }
}
