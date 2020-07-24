package com.silita.biaodaa.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

public class LawTest extends ConfigTest {

    @Autowired
    private UnderConstructService underConstructService;

    @org.junit.Test
    public void test() {
        Map<String, Object> param = new HashMap<String, Object>(1) {{
            put("idCard", "430922198909120033");
        }};
        System.out.println(underConstructService.getApiUnderConstruct(param));
    }
}
