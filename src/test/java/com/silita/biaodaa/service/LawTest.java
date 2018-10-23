package com.silita.biaodaa.service;

import com.silita.biaodaa.task.LawTask;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LawTest extends ConfigTest{

    @Test
    public void test(){
        LawTask lawTask = new LawTask();
        Thread t = new Thread(lawTask);
        t.start();
    }

}
