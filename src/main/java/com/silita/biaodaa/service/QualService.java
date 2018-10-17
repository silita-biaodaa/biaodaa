package com.silita.biaodaa.service;

import com.silita.biaodaa.model.TbCompanyQualification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QualService {

    /**
     * 资质排序
     *
     * @param qualList
     * @return
     */
    public List<TbCompanyQualification> sortCompanyQual(List<TbCompanyQualification> qualList, String qualType) {
        if ("建筑业企业资质".equals(qualType)) {
            return sortQual(qualList);
        }
        //排序等级
        List<TbCompanyQualification> result = this.sortQualGrade(this.sortQualGrade(qualList));
        return result;
    }

    /**
     * 施工资质分类
     *
     * @param qualList
     * @return
     */
    private List<TbCompanyQualification> sortQual(List<TbCompanyQualification> qualList) {
        List<TbCompanyQualification> resultList = new ArrayList<>();
        List<TbCompanyQualification> contractList = new ArrayList<>();
        List<TbCompanyQualification> majorList = new ArrayList<>();
        List<TbCompanyQualification> buildList = new ArrayList<>();
        for (int i = 0; i < qualList.size(); i++) {
            if (qualList.get(i).getQualName().contains("总承包")) {
                contractList.add(qualList.get(i));
            } else if (qualList.get(i).getQualName().contains("专业承包")) {
                majorList.add(qualList.get(i));
            } else {
                buildList.add(qualList.get(i));
            }
        }
        if (null != contractList && contractList.size() > 0) {
            resultList.addAll(this.sortQualGrade(this.sortQualGrade(this.sortQualType(contractList))));
        }
        if (null != majorList && majorList.size() > 0) {
            resultList.addAll(sortQualGrade(majorList));
        }
        if (null != buildList && buildList.size() > 0) {
            resultList.addAll(buildList);
        }
        return resultList;
    }

    /**
     * 资质等级
     *
     * @param qualList
     * @return
     */
    private List<TbCompanyQualification> sortQualGrade(List<TbCompanyQualification> qualList) {
        List<TbCompanyQualification> resultList = new ArrayList<>();
        int index = -1;
        for (int i = 0; i < qualList.size(); i++) {
            if (qualList.get(i).getQualName().contains("特级")) {
                index = getIndex(resultList, "特级") + 1;
            } else if (qualList.get(i).getQualName().contains("壹级")) {
                index = getIndex3(resultList, "特级", "壹级") + 1;

            } else if (qualList.get(i).getQualName().contains("贰级")) {
                index = getIndex3(resultList, "壹级", "贰级") + 1;
            } else if (qualList.get(i).getQualName().contains("叁级")) {
                index = getIndex3(resultList, "贰级", "叁级") + 1;
            } else if (qualList.get(i).getQualName().contains("甲级")) {
                index = getIndex(resultList, "甲级") + 1;
            } else if (qualList.get(i).getQualName().contains("乙级")) {
                index = getIndex3(resultList, "甲级", "乙级") + 1;
            } else if (qualList.get(i).getQualName().contains("丙级")) {
                index = getIndex3(resultList, "乙级", "丙级") + 1;
            }
            resultList.add(index, qualList.get(i));
        }
        return resultList;
    }

    /**
     * 同等级按不同专业排序
     *
     * @param qualList
     * @return
     */
    private List<TbCompanyQualification> sortQualType(List<TbCompanyQualification> qualList) {
        List<TbCompanyQualification> resultList = new ArrayList<>();
        int index = -1;
        for (int i = 0; i < qualList.size(); i++) {
            if (qualList.get(i).getQualName().contains("总承包")) {
                if (qualList.get(i).getQualName().contains("建筑")) {
                    if (qualList.get(i).getQualName().contains("特级")) {
                        index = 0;
                    } else {
                        index = getGradeIndex(resultList, "市政", "市政", "建筑", "建筑");
                    }
                    resultList.add(index, qualList.get(i));
                } else if (qualList.get(i).getQualName().contains("市政")) {
                    index = getGradeIndex(resultList, "建筑", "建筑", "市政", "市政");
                    if (index > 0) {
                        resultList.add(index, qualList.get(i));
                    } else {
                        resultList.add(qualList.get(i));
                    }
                } else if (qualList.get(i).getQualName().contains("公路")) {
                    index = getGradeIndex(resultList, "市政", "建筑", "公路", "公路");
                    if (index > 0) {
                        resultList.add(index, qualList.get(i));
                    } else {
                        resultList.add(qualList.get(i));
                    }
                } else if (qualList.get(i).getQualName().contains("水利")) {
                    index = getGradeIndex(resultList, "公路", "市政", "建筑", "水利");
                    if (index > 0) {
                        resultList.add(index, qualList.get(i));
                    } else {
                        resultList.add(qualList.get(i));
                    }
                } else {
                    resultList.add(qualList.get(i));
                }
            } else {
                resultList.add(qualList.get(i));
            }
        }
        return resultList;
    }

    /**
     * 获取下标
     *
     * @param resultList
     * @param value
     * @return
     */
    private int getIndex(List<TbCompanyQualification> resultList, String... value) {
        int index = -1;
        index = getLastIndex(resultList, value);
        if (index == -1 && value.length > 1) {
            index = getLastIndex(resultList, value[1]);
        }
        return index;
    }

    /**
     * 获取下标
     *
     * @param resultList
     * @param value
     * @return
     */
    private int getIndex3(List<TbCompanyQualification> resultList, String... value) {
        int index = -1;
        index = getLastIndex(resultList, value[0]);
        if (index == -1) {
            index = getLastIndex(resultList, value[1]);
        }
        return index;
    }

    /**
     * 获取下标
     *
     * @param resultList
     * @param value
     * @return
     */
    private int getLastIndex(List<TbCompanyQualification> resultList, String... value) {
        int index = -1;
        for (int i = resultList.size() - 1; i >= 0; i--) {
            if (value.length <= 1) {
                if (resultList.get(i).getQualName().contains(value[0])) {
                    index = i;
                    break;
                }
            }
            if (resultList.get(i).getQualName().contains(value[0]) && resultList.get(i).getQualName().contains(value[1])) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 获取下标
     *
     * @param resultList
     * @param value
     * @return
     */
    private int getLastIndex2(List<Map<String, Object>> resultList, String... value) {
        int index = -1;
        for (int i = resultList.size() - 1; i >= 0; i--) {
            if (value.length <= 1) {
                if (resultList.get(i).get("qualType").toString().contains(value[0])) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * 获取下标
     *
     * @param resultList
     * @param value
     * @return
     */
    private int getLastIndex3(List<TbCompanyQualification> resultList, String... value) {
        int index = -1;
        for (int i = resultList.size() - 1; i >= 0; i--) {
            if (resultList.get(i).getQualName().contains(value[0])
                    || resultList.get(i).getQualName().contains(value[1])
                    || resultList.get(i).getQualName().contains(value[2])
                    || resultList.get(i).getQualName().contains(value[3])) {
                index = i;
                break;
            }
        }
        return index;
    }

    private int getGradeIndex(List<TbCompanyQualification> resultList, String... value) {
        int index = getLastIndex3(resultList, value[0], value[1], value[2], value[3]) + 1;
        return index;
    }

    /**
     * 排序类别
     *
     * @param list
     * @return
     */
    public List<Map<String, Object>> sortQualType2(List<Map<String, Object>> list) {
        List<Map<String, Object>> qualList = new ArrayList<>();
        int index;
        for (Map<String, Object> map : list) {
            if ("建筑业".equals(map.get("qualType"))) {
                qualList.add(0, map);
            } else if ("设计与施工".equals(map.get("qualType"))) {
                index = getLastIndex2(qualList, "建筑业");
                if (index == -1) {
                    qualList.add(map);
                } else {
                    qualList.add(getLastIndex2(qualList, "建筑业") + 1, map);
                }
            } else if ("公路养护".equals(map.get("qualType"))) {
                index = getLastIndex2(qualList, "设计施工一体化");
                if (index == -1) {
                    qualList.add(map);
                } else {
                    qualList.add(getLastIndex2(qualList, "设计施工一体化") + 1, map);
                }
            } else if ("工程设计".equals(map.get("qualType"))) {
                index = getLastIndex2(qualList, "公路养护");
                if (index == -1) {
                    qualList.add(map);
                } else {
                    qualList.add(getLastIndex2(qualList, "公路养护") + 1, map);
                }
            } else if ("工程勘察".equals(map.get("qualType"))) {
                index = getLastIndex2(qualList, "工程设计");
                if (index == -1) {
                    qualList.add(map);
                } else {
                    qualList.add(getLastIndex2(qualList, "工程设计") + 1, map);
                }
            } else if ("工程监理".equals(map.get("qualType"))) {
                index = getLastIndex2(qualList, "工程勘察");
                if (index == -1) {
                    qualList.add(map);
                } else {
                    qualList.add(getLastIndex2(qualList, "工程勘察") + 1, map);
                }
            } else {
                qualList.add(map);
            }
        }
        return qualList;
    }
}
