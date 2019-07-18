package com.silita.biaodaa.service;

import org.apache.commons.collections.map.HashedMap;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QualTest extends ConfigTest{

    @Autowired
    TbCompanyService tbCompanyService;

    @Test
    public void test() {
        String qualStr = "消防设施工程专业承包贰级,防水防腐保温工程专业承包贰级,建筑装修装饰工程专业承包贰级,建筑幕墙工程专业承包贰级,水利水电工程施工总承包叁级,建筑工程施工总承包叁级,公路工程施工总承包叁级,市政公用工程施工总承包叁级,机电工程施工总承包叁级,电力工程施工总承包叁级,矿山工程施工总承包叁级,石油化工工程施工总承包叁级,公路路基工程专业承包叁级,河湖整治工程专业承包叁级,公路路面工程专业承包叁级,环保工程专业承包叁级,地基基础工程专业承包叁级,桥梁工程专业承包叁级,隧道工程专业承包叁级,钢结构工程专业承包叁级,建筑机电安装工程专业承包叁级,古建筑工程专业承包叁级,城市及道路照明工程专业承包叁级,施工劳务不分等级";
        String[] qual = qualStr.split(",");
        List<Map<String, Object>> qualList = new ArrayList<>();
        Map<String, Object> map;
        for (int i = 0; i < qual.length; i++) {
            map = new HashMap<>();
            map.put("key", (i + 1));
            map.put("value", qual[i]);
            qualList.add(map);
        }

        //排序等级
//        List<Map<String, Object>> qualGradeList = this.sortQualGrade(qualList);
        //资质同等级排序
        List<Map<String, Object>> quals = this.sortQualType(qualList);
        //排序资质类别
//        List<Map<String, Object>> sortList = this.sortQual(quals);
        //资质按等级排序
        for (Map<String, Object> val : quals) {
            System.out.println(val.get("value"));
        }
    }

    private List<Map<String, Object>> sortQual(List<Map<String, Object>> qualList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        int count = -1;
        for (int i = 0; i < qualList.size(); i++) {
            if (qualList.get(i).get("value").toString().contains("总承包")) {
                resultList.add(getLastIndex(resultList, "总承包") + 1, qualList.get(i));
            } else if (qualList.get(i).get("value").toString().contains("专业承包")) {
                resultList.add(getLastIndex(resultList, "总承包") + 1, qualList.get(i));
            } else {
                resultList.add(getLastIndex(resultList, "施工劳务") + 1, qualList.get(i));
            }
        }
        return resultList;
    }

    private List<Map<String, Object>> sortQualGrade(List<Map<String, Object>> qualList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < qualList.size(); i++) {
            if (qualList.get(i).get("value").toString().contains("特级")) {
                resultList.add(getIndex(resultList, "特级") + 1, qualList.get(i));
            } else if (qualList.get(i).get("value").toString().contains("壹级")) {
                resultList.add(getIndex(resultList, "特级", "壹级") + 1, qualList.get(i));
            } else if (qualList.get(i).get("value").toString().contains("贰级")) {
                resultList.add(getIndex(resultList, "壹级", "贰级") + 1, qualList.get(i));
            } else if (qualList.get(i).get("value").toString().contains("叁级")) {
                resultList.add(getIndex(resultList, "贰级", "叁级") + 1, qualList.get(i));
            } else if (qualList.get(i).get("value").toString().contains("甲级")) {
                resultList.add(getIndex(resultList, "甲级") + 1, qualList.get(i));
            } else if (qualList.get(i).get("value").toString().contains("乙级")) {
                resultList.add(getIndex(resultList, "甲级", "乙级") + 1, qualList.get(i));
            } else if (qualList.get(i).get("value").toString().contains("丙级")) {
                resultList.add(getIndex(resultList, "乙级", "丙级") + 1, qualList.get(i));
            }
        }
        return resultList;
    }

    private List<Map<String, Object>> sortQualType(List<Map<String, Object>> qualList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        int index = -1;
        for (int i = 0; i < qualList.size(); i++) {
            if (qualList.get(i).get("value").toString().contains("总承包")) {
                if (qualList.get(i).get("value").toString().contains("建筑")) {
                    if (qualList.get(i).get("value").toString().contains("特级")) {
                        index = 0;
                    } else {
                        index = getGradeIndex(resultList, "市政", "市政", "建筑", "建筑");
                    }
                    resultList.add(index, qualList.get(i));
                } else if (qualList.get(i).get("value").toString().contains("市政")) {
                    index = getGradeIndex(resultList, "建筑", "建筑", "市政", "市政");
                    if (index > 0) {
                        resultList.add(index, qualList.get(i));
                    } else {
                        resultList.add(qualList.get(i));
                    }
                } else if (qualList.get(i).get("value").toString().contains("公路")) {
                    index = getGradeIndex(resultList, "市政", "建筑", "公路", "公路");
                    if (index > 0) {
                        resultList.add(index, qualList.get(i));
                    } else {
                        resultList.add(qualList.get(i));
                    }
                } else if (qualList.get(i).get("value").toString().contains("水利")) {
                    index = getGradeIndex(resultList, "公路", "市政", "建筑", "水利");
                    if (index > 0) {
                        resultList.add(index, qualList.get(i));
                    } else {
                        resultList.add(qualList.get(i));
                    }
                } else {
                    if (qualList.get(i).get("value").toString().contains("特级")) {
                        index = getGradeIndex2(resultList, "水利", "公路", "市政", "建筑", "特级", "特级");
                    } else if (qualList.get(i).get("value").toString().contains("壹级")) {
                        index = getGradeIndex2(resultList, "水利", "公路", "市政", "建筑", "壹级", "特级");
                    } else if (qualList.get(i).get("value").toString().contains("贰级")) {
                        index = getGradeIndex2(resultList, "水利", "公路", "市政", "建筑", "贰级", "壹级");
                    } else if (qualList.get(i).get("value").toString().contains("叁级")) {
                        index = getGradeIndex2(resultList, "水利", "公路", "市政", "建筑", "叁级", "贰级");
                    }
                    if (index > 0) {
                        resultList.add(index, qualList.get(i));
                    } else {
                        resultList.add(qualList.get(i));
                    }
                }
            } else {
                resultList.add(qualList.get(i));
            }
        }
        return resultList;
    }

    private int getIndex(List<Map<String, Object>> resultList, String... value) {
        int index = -1;
        index = getLastIndex(resultList, value);
        if (index == -1 && value.length > 1) {
            index = getLastIndex(resultList, value[1]);
        }
        return index;
    }

    private int getLastIndex(List<Map<String, Object>> resultList, String... value) {
        int index = -1;
        for (int i = resultList.size() - 1; i >= 0; i--) {
            if (value.length <= 1) {
                if (resultList.get(i).get("value").toString().contains(value[0])) {
                    index = i;
                    break;
                }
            }
            if (resultList.get(i).get("value").toString().contains(value[0]) && resultList.get(i).get("value").toString().contains(value[1])) {
                index = i;
                break;
            }
        }
        return index;
    }

    private int getGradeIndex(List<Map<String, Object>> resultList, String... value) {
        int index = getLastIndex2(resultList, value[0], value[1], value[2], value[3]) + 1;
        return index;
    }

    private int getGradeIndex2(List<Map<String, Object>> resultList, String... value) {
        int index = getLastIndex3(resultList, value[0], value[1], value[2], value[3], value[4], value[5]) + 1;
        return index;
    }

    private int getLastIndex3(List<Map<String, Object>> resultList, String... value) {
        int index = -1;
        for (int i = resultList.size() - 1; i >= 0; i--) {
            if (resultList.get(i).get("value").toString().contains("总承包")) {
                if ((resultList.get(i).get("value").toString().contains(value[0])
                        || resultList.get(i).get("value").toString().contains(value[1])
                        || resultList.get(i).get("value").toString().contains(value[2])
                        || resultList.get(i).get("value").toString().contains(value[3]))
                        && (resultList.get(i).get("value").toString().contains(value[4])
                        || resultList.get(i).get("value").toString().contains(value[5]))) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    private int getLastIndex2(List<Map<String, Object>> resultList, String... value) {
        int index = -1;
        for (int i = resultList.size() - 1; i >= 0; i--) {
            if (resultList.get(i).get("value").toString().contains("总承包")) {
                if (resultList.get(i).get("value").toString().contains(value[0])
                        || resultList.get(i).get("value").toString().contains(value[1])
                        || resultList.get(i).get("value").toString().contains(value[2])
                        || resultList.get(i).get("value").toString().contains(value[3])) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    @Test
    public void setQual() {
        Map<String, Object> param = new HashedMap() {{
//            put("qualCode", "qual_tdxy_1554281896934/grade_yjjys_1554256688513,qual_zhzz_1554281838972,qual_gczbdl_1554281840493/grade_yj_1553245789137");
            put("qualCode", "qual_tdxy_1554281896934/grade_yjjys_1554256688513,qual_tdxy_1554281896934/grade_yjjys_1554256688513,qual_tdxy_1554281896934/grade_yjjys_1554256688513");
            put("rangeType", "or");
        }};
        tbCompanyService.setNewQualCode(param);
        System.out.println(param);
    }
}
