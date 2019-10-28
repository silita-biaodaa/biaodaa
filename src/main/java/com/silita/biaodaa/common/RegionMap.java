package com.silita.biaodaa.common;

import org.apache.commons.collections.map.HashedMap;

import java.util.HashMap;
import java.util.Map;

/**
 * 地区相关map
 * Created by zhushuai on 2019/9/26.
 */
public class RegionMap {

    /**
     * 获取省code
     */
    public static Map<String, String> regionSource = new HashMap<>(31);

    /**
     * 获取省名称
     */
    public static Map<String, String> regionSourceCode = new HashMap<>(31);

    /**
     * 获取省的备案下标
     */
    public static Map<String, Integer> regionBeiAnIndex = new HashMap<>(31);

    /**
     * 获取省的简写
     */
    public static Map<String, String> regionSimpleMap = new HashedMap(31);

    static {
        regionSourceCode.put("安徽省", "anh");
        regionSourceCode.put("北京市", "beij");
        regionSourceCode.put("重庆市", "chongq");
        regionSourceCode.put("福建省", "fuj");
        regionSourceCode.put("甘肃省", "gans");
        regionSourceCode.put("广东省", "guangd");
        regionSourceCode.put("广西壮族自治区", "guangx");
        regionSourceCode.put("贵州省", "guiz");
        regionSourceCode.put("海南省", "hain");
        regionSourceCode.put("河北省", "hebei");
        regionSourceCode.put("黑龙江省", "heilj");
        regionSourceCode.put("河南省", "henan");
        regionSourceCode.put("湖北省", "hubei");
        regionSourceCode.put("湖南省", "hunan");
        regionSourceCode.put("江苏省", "jiangs");
        regionSourceCode.put("江西省", "jiangx");
        regionSourceCode.put("吉林省", "jil");
        regionSourceCode.put("辽宁省", "liaon");
        regionSourceCode.put("内蒙古自治区", "neimg");
        regionSourceCode.put("宁夏回族自治区", "ningx");
        regionSourceCode.put("青海省", "qingh");
        regionSourceCode.put("山西省", "sanx");
        regionSourceCode.put("山东省", "shand");
        regionSourceCode.put("上海市", "shangh");
        regionSourceCode.put("陕西省", "shanxi");
        regionSourceCode.put("四川省", "sichuan");
        regionSourceCode.put("天津市", "tianj");
        regionSourceCode.put("新疆维吾尔自治区", "xinjiang");
        regionSourceCode.put("西藏自治区", "xizang");
        regionSourceCode.put("云南省", "yunn");
        regionSourceCode.put("浙江省", "zhej");

        regionSource.put("anh", "安徽省");
        regionSource.put("beij", "北京市");
        regionSource.put("chongq", "重庆市");
        regionSource.put("fuj", "福建省");
        regionSource.put("gans", "甘肃省");
        regionSource.put("guangd", "广东省");
        regionSource.put("guangx", "广西壮族自治区");
        regionSource.put("guiz", "贵州省");
        regionSource.put("hain", "海南省");
        regionSource.put("hebei", "河北省");
        regionSource.put("heilj", "黑龙江省");
        regionSource.put("henan", "河南省");
        regionSource.put("hubei", "湖北省");
        regionSource.put("hunan", "湖南省");
        regionSource.put("jiangs", "江苏省");
        regionSource.put("jiangx", "江西省");
        regionSource.put("jil", "吉林省");
        regionSource.put("liaon", "辽宁省");
        regionSource.put("neimg", "内蒙古自治区");
        regionSource.put("ningx", "宁夏回族自治区");
        regionSource.put("qingh", "青海省");
        regionSource.put("sanx", "山西省");
        regionSource.put("shand", "山东省");
        regionSource.put("shangh", "上海市");
        regionSource.put("shanxi", "陕西省");
        regionSource.put("sichuan", "四川省");
        regionSource.put("tianj", "天津市");
        regionSource.put("xinjiang", "新疆维吾尔自治区");
        regionSource.put("xizang", "西藏自治区");
        regionSource.put("yunn", "云南省");
        regionSource.put("zhej", "浙江省");

        regionBeiAnIndex.put("安徽省", 11);
        regionBeiAnIndex.put("北京市", 0);
        regionBeiAnIndex.put("重庆市", 20);
        regionBeiAnIndex.put("福建省", 12);
        regionBeiAnIndex.put("甘肃省", 25);
        regionBeiAnIndex.put("广东省", 17);
        regionBeiAnIndex.put("广西壮族自治区", 18);
        regionBeiAnIndex.put("贵州省", 22);
        regionBeiAnIndex.put("海南省", 19);
        regionBeiAnIndex.put("河北省", 2);
        regionBeiAnIndex.put("黑龙江省", 7);
        regionBeiAnIndex.put("河南省", 15);
        regionBeiAnIndex.put("湖北省", 16);
        regionBeiAnIndex.put("湖南省", 28);
        regionBeiAnIndex.put("江苏省", 9);
        regionBeiAnIndex.put("江西省", 13);
        regionBeiAnIndex.put("吉林省", 6);
        regionBeiAnIndex.put("辽宁省", 5);
        regionBeiAnIndex.put("内蒙古自治区", 4);
        regionBeiAnIndex.put("宁夏回族自治区", 27);
        regionBeiAnIndex.put("青海省", 26);
        regionBeiAnIndex.put("山西省", 3);
        regionBeiAnIndex.put("山东省", 14);
        regionBeiAnIndex.put("上海市", 8);
        regionBeiAnIndex.put("陕西省", 24);
        regionBeiAnIndex.put("四川省", 21);
        regionBeiAnIndex.put("天津市", 1);
        regionBeiAnIndex.put("新疆维吾尔自治区", 29);
        regionBeiAnIndex.put("西藏自治区", 30);
        regionBeiAnIndex.put("云南省", 23);
        regionBeiAnIndex.put("浙江省", 10);

        regionSimpleMap.put("安徽省", "皖");
        regionSimpleMap.put("北京市", "京");
        regionSimpleMap.put("重庆市", "渝");
        regionSimpleMap.put("福建省", "闽");
        regionSimpleMap.put("甘肃省", "甘");
        regionSimpleMap.put("广东省", "粤");
        regionSimpleMap.put("广西壮族自治区", "桂");
        regionSimpleMap.put("贵州省", "黔");
        regionSimpleMap.put("海南省", "琼");
        regionSimpleMap.put("河北省", "冀");
        regionSimpleMap.put("黑龙江省", "黑");
        regionSimpleMap.put("河南省", "豫");
        regionSimpleMap.put("湖北省", "鄂");
        regionSimpleMap.put("湖南省", "湘");
        regionSimpleMap.put("江苏省", "苏");
        regionSimpleMap.put("江西省", "赣");
        regionSimpleMap.put("吉林省", "吉");
        regionSimpleMap.put("辽宁省", "辽");
        regionSimpleMap.put("内蒙古自治区", "蒙");
        regionSimpleMap.put("宁夏回族自治区", "宁");
        regionSimpleMap.put("青海省", "青");
        regionSimpleMap.put("山西省", "晋");
        regionSimpleMap.put("山东省", "鲁");
        regionSimpleMap.put("上海市", "沪");
        regionSimpleMap.put("陕西省", "陕");
        regionSimpleMap.put("四川省", "川");
        regionSimpleMap.put("天津市", "津");
        regionSimpleMap.put("新疆维吾尔自治区", "新");
        regionSimpleMap.put("西藏自治区", "藏");
        regionSimpleMap.put("云南省", "云");
        regionSimpleMap.put("浙江省", "浙");
    }

}
