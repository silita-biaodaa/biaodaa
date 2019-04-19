package com.silita.biaodaa.service;

import com.silita.biaodaa.service.impl.PDFServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by zhushuai on 2019/4/19.
 */
public class ProjectTest extends ConfigTest {

    @Autowired
    PDFServiceImpl pdfService;

    @org.junit.Test
    public void test(){
        pdfService.buildPdf("123");
    }
}
