package com.silita.biaodaa.service;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;

public class Test extends ConfigTest {

    @Autowired
    LawService lawService;
    @Autowired
    UnderConstructService underConstructService;

    @org.junit.Test
    public void test() {
        underConstructService.getApiUnderConstruct(new HashedMap(1) {{
            put("idCard", "220104197310014218");
        }});
    }
}
