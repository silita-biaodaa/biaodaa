package com.silita.biaodaa.service;

import com.silita.biaodaa.controller.vo.Page;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@TransactionConfiguration(transactionManager="txManager",defaultRollback=true)
@WebAppConfiguration
public class NoticeServiceTest extends ConfigTest {

    @Autowired
    NoticeService noticeService;

    @Autowired
    private CommonService commonService;

    @Test
    public void testZz(){
        List<Map<String, Object>> list= commonService.queryPbMode();
        System.out.println("PbMode.size:"+list.size());
        List<Map<String, Object>> zzList= commonService.queryCertZzByCompanyName("中南建设集团有限公司");
        System.out.println("zzList.size:"+zzList.size());
    }

    @Test
    public void testClickRecord(){
        commonService.insertRecordLog("zhongbiaoTest", "11111", "1.1.1.1");
        commonService.insertRecordLog("zhaobiaoTest", "11111", "222.222.2.2");
    }


    protected MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext wac;

    @Before()  //这个方法在每个方法执行之前都会执行一遍
    public void setup()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();  //初始化MockMvc对象
    }

    /**
     * perform：执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
     * get:声明发送一个get请求的方法。MockHttpServletRequestBuilder get(String urlTemplate, Object... urlVariables)：根据uri模板<span style="white-space:pre;">      </span>和uri变量值得到一个GET请求方式的。另外提供了其他的请求的方法，如：post、put、delete等。
     * param：添加request的参数，如上面发送请求的时候带上了了pcode = root的参数。假如使用需要发送json数据格式的时将不能使用这种<span style="white-space:pre;">        </span>方式，可见后面被@ResponseBody注解参数的解决方法
     * andExpect：添加ResultMatcher验证规则，验证控制器执行完成后结果是否正确（对返回的数据进行的判断）；
     * andDo：添加ResultHandler结果处理器，比如调试时打印结果到控制台（对返回的数据进行的判断）；
     * andReturn：最后返回相应的MvcResult；然后进行自定义验证/进行下一步的异步处理（对返回的数据进行的判断）
     * @throws Exception
     */
    @Test
    public void testController()throws Exception{//,"pbModes":"合理定价评审抽取法||综合评估法Ⅰ" projSumEnd 2018-04-01  2018-05-30 "pbModes":"合理定价评审抽取法||综合评估法Ⅰ", ,"projSumStart":"100","projSumEnd":"500","zzType":"main_type||4c7d025c-2934-11e5-a311-63b86f04c8dd||4c7d025c-2934-11e5-a311-63b86f04c8dd/3"
        String requestBody = "{\"pageNo\":29,\"pageSize\":20,\"type\":0,\"kbDateStart\":\"2018-04-12\",\"kbDateEnd\":\"2018-04-13\",\"regions\":\"||长沙市\"}";
        String responseString = mockMvc.perform(post("/notice/queryList").characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                        .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-----返回的json = " + responseString);
    }


    @Test
    public void testNoticeService(){
        Page page = new Page();
        page.setPageSize(20);
        page.setCurrentPage(1);

        Map param=new HashMap();
        param.put("type","0");
        noticeService.searchNoticeList(page,param);
    }

}
