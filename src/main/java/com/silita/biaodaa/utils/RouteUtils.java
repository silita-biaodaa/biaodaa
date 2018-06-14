package com.silita.biaodaa.utils;


public class RouteUtils {
    public static final String HUNAN_SOURCE = "hunan";

    public static String routeTableName(String tbName, String source) {
        if(MyStringUtils.isNull(source) || source.equals(HUNAN_SOURCE)){
            return tbName;
        }else{
            return tbName+"_"+source;
        }
    }

    public static String parseProvinceToSource(String province){
        String source =null;
        if(MyStringUtils.isNotNull(province)){
            switch (province){
                case"北京":source="beij";break;
                case"天津":source="tianj";break;
                case"河北":source="hebei";break;
                case"山西":source="sanx";break;
                case"内蒙":source="neimg";break;
                case"辽宁":source="liaon";break;
                case"吉林":source="jil";break;
                case"黑龙":source="heilj";break;
                case"上海":source="shangh";break;
                case"江苏":source="jiangs";break;
                case"浙江":source="zhej";break;
                case"安徽":source="anh";break;
                case"福建":source="fuj";break;
                case"江西":source="jiangx";break;
                case"山东":source="shand";break;
                case"河南":source="henan";break;
                case"湖北":source="hubei";break;
                case"广东":source="guangd";break;
                case"广西":source="guangx";break;
                case"海南":source="hain";break;
                case"重庆":source="chongq";break;
                case"四川":source="sichuan";break;
                case"贵州":source="guiz";break;
                case"云南":source="yunn";break;
                case"陕西":source="shanxi";break;
                case"甘肃":source="gans";break;
                case"青海":source="qingh";break;
                case"宁夏":source="ningx";break;
                case"湖南":source=HUNAN_SOURCE;break;
                case"新疆":source="xinjiang";break;
                case"西藏":source="xizang";break;
                case"香港":source="hk";break;
                case"澳门":source="macau";break;
                case"台湾":source="taiwan";break;
            }
        }else{
            source=HUNAN_SOURCE;
        }
        return source;
    }

    public static String parseSourceToProv(String source){
        String province=null;
        if(MyStringUtils.isNotNull(source)){
            switch(source){
                case"beij":province="北京";break;
                case"tianj":province="天津";break;
                case"hebei":province="河北";break;
                case"sanx":province="山西";break;
                case"neimg":province="内蒙";break;
                case"liaon":province="辽宁";break;
                case"jil":province="吉林";break;
                case"heilj":province="黑龙";break;
                case"shangh":province="上海";break;
                case"jiangs":province="江苏";break;
                case"zhej":province="浙江";break;
                case"anh":province="安徽";break;
                case"fuj":province="福建";break;
                case"jiangx":province="江西";break;
                case"shand":province="山东";break;
                case"henan":province="河南";break;
                case"hubei":province="湖北";break;
                case"guangd":province="广东";break;
                case"guangx":province="广西";break;
                case"hain":province="海南";break;
                case"chongq":province="重庆";break;
                case"sichuan":province="四川";break;
                case"guiz":province="贵州";break;
                case"yunn":province="云南";break;
                case"shanxi":province="陕西";break;
                case"gans":province="甘肃";break;
                case"qingh":province="青海";break;
                case"ningx":province="宁夏";break;
                case HUNAN_SOURCE:province="湖南";break;
                case"xinjiang":province="新疆";break;
                case"xizang":province="西藏";break;
                case"hk":province="香港";break;
                case"macau":province="澳门";break;
                case"taiwan":province="台湾";break;
            }
        }else{
            province="湖南";
        }
        return province;
    }
}
