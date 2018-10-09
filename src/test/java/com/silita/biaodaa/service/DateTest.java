package com.silita.biaodaa.service;

import com.silita.biaodaa.utils.MyDateUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class DateTest {

    @Test
    public void test() {
        System.out.println(MyDateUtils.longDateToStr(0L, "yyyy-MM-dd"));
    }

    @Test
    public void test2() {
        List<String> list = new ArrayList<>();
        list.add("bbb");
        list.add("ccc");
        list.add("aaa");
        List<String> sortList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if(list.get(i).contains("a")){
                sortList.add(0,list.get(i));
            }else if(list.get(i).contains("b")){
                sortList.add(sortList.lastIndexOf(sortList.contains("a"))+1,list.get(i));
            }else {
                sortList.add(list.get(i));
            }
        }

        for(String str : sortList){
            System.out.println(str);
        }
    }
}
