package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.model.Page;
import com.silita.pay.service.OrderInfoService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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

    @Autowired
    private OrderInfoService orderInfoService;

    @Test
    public void testDubbo(){
//        PageImpl<OrderInfo> list = orderInfoService.queryOrderPages(null,0,5);
//        for(OrderInfo o: list.getContent()){
//            System.out.println(o.getOrderNo());
//        }
    }

    @Test
    public void testBulidStatComCount(){
        HashMap arg = new HashMap(2);
        arg.put("startDay","2017-01-01");
        arg.put("endDay","2017-01-10");
        String comType = "1"; //0:湖南本地 1：入湘
        noticeService.bulidStatComCount(arg,comType);
        comType = "0"; //0:湖南本地 1：入湘
        noticeService.bulidStatComCount(arg,comType);
    }

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
        try {
            token = initToken();
        }catch (Exception e){
            logger.error(e,e);
        }
    }

    @Test
    public void testLogin() throws Exception{
//        initToken();
    }

    private String initToken()throws Exception{
        String requestBody = "{\"username\":\"daihuan\",\"userpass\":\"7c222fb2927d828af22f592134e8932480637c0d\",\"userphone\":\"\",\"loginchannel\":\"1001\",\"version\":\"10611\"}";
        String responseString = mockMvc.perform(post("/authorize/userLogin").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
//                .header("X-TOKEN",token)
        )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-----返回的json = " + responseString);
        JSONObject jobj = JSON.parseObject(responseString);
        JSONObject data = (JSONObject)jobj.get("data");
        String token = null;
        if(data != null) {
            token = data.getString("xtoken");
            System.out.println(token);
        }
        return token;
    }

    //53538939f6dc4fd38feb9b54a93075d3
    String token=null;

    /**
     * perform：执行一个RequestBuilder请求，会自动执行SpringMVC的流程并映射到相应的控制器执行处理；
     * get:声明发送一个get请求的方法。MockHttpServletRequestBuilder get(String urlTemplate, Object... urlVariables)：根据uri模板<span style="white-space:pre;">      </span>和uri变量值得到一个GET请求方式的。另外提供了其他的请求的方法，如：post、put、delete等。
     * param：添加request的参数，如上面发送请求的时候带上了了pcode = root的参数。假如使用需要发送json数据格式的时将不能使用这种<span style="white-space:pre;">        </span>方式，可见后面被@ResponseBody注解参数的解决方法
     * andExpect：添加ResultMatcher验证规则，验证控制器执行完成后结果是否正确（对返回的数据进行的判断）；
     * andDo：添加ResultHandler结果处理器，比如调试时打印结果到控制台（对返回的数据进行的判断）；
     * andReturn：最后返回相应的MvcResult；然后进行自定义验证/进行下一步的异步处理（对返回的数据进行的判断）
     * @throws Exception
     */
    @Test  //,"zzType":"main_type||4c7f85db-2934-11e5-a311-63b86f04c8dd||4c7f85db-2934-11e5-a311-63b86f04c8dd/2"  "com_name":"湖南省联能电力建设有限公司",16577027-2939-11e5-a311-63b86f04c8dd/3
    public void testqueryList()throws Exception{//,"projectType":"0","regions":"北京市||","regions":"湖南省||长沙市","projectType":"0","title":"小东","projSumStart":"0","projSumEnd":"1000","regions":"湖南省||长沙市","pbModes":"合理定价评审抽取法||综合评估法Ⅰ" ,,"pbModes":"合理定价评审抽取法||综合评估法Ⅰ","projSumStart":"500","projSumEnd":"1000" projSumEnd 2018-04-01  2018-05-30 "pbModes":"合理定价评审抽取法||综合评估法Ⅰ",,"kbDateStart":"2018-01-30","kbDateEnd":"2018-03-13", ,"projSumStart":"100","projSumEnd":"500","zzType":"main_type||4c7d025c-2934-11e5-a311-63b86f04c8dd||4c7d025c-2934-11e5-a311-63b86f04c8dd/3"
        //,"com_name":"湖南耀邦建设有限公司","zzType":"main_type||4c7f85db-2934-11e5-a311-63b86f04c8dd||16573c34-2939-11e5-a311-63b86f04c8dd/11"  main_type||4c7d025c-2934-11e5-a311-63b86f04c8dd||4c7d025c-2934-11e5-a311-63b86f04c8dd/3
        //,"zzType":"main_type||||,main_type33333||||,main_type222||||" ,"title":"高管"
        String requestBody = "{\"pageNo\":1,\"pageSize\":20,\"type\":2,\"projectType\":\"0\"}";
//        requestBody ="{\"regions\":\"\",\"type\":\"2\",\"com_name\":\"湖南耀邦建设有限公司\"} ";
        String responseString = mockMvc.perform(post("/notice/queryList").characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                        .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                        .header("X-TOKEN",token)
                )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-----返回的json = " + responseString);
    }

    @Test  //,"zzType":"main_type||4c7f85db-2934-11e5-a311-63b86f04c8dd||4c7f85db-2934-11e5-a311-63b86f04c8dd/2"
    public void testSearchList()throws Exception{//,"regions":"福建省||","regions":"湖南省||长沙市","projectType":"0","projSumStart":"0","projSumEnd":"1000","regions":"湖南省||长沙市","pbModes":"合理定价评审抽取法||综合评估法Ⅰ" ,,"pbModes":"合理定价评审抽取法||综合评估法Ⅰ","projSumStart":"500","projSumEnd":"1000" projSumEnd 2018-04-01  2018-05-30 "pbModes":"合理定价评审抽取法||综合评估法Ⅰ",,"kbDateStart":"2018-01-30","kbDateEnd":"2018-03-13", ,"projSumStart":"100","projSumEnd":"500","zzType":"main_type||4c7d025c-2934-11e5-a311-63b86f04c8dd||4c7d025c-2934-11e5-a311-63b86f04c8dd/3"
        String requestBody = "{\"pageNo\":1,\"pageSize\":20,\"type\":99}";
        String responseString = mockMvc.perform(post("/notice/searchList").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                .header("X-TOKEN",token)
        )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-----返回的json = " + responseString);
    }

    @Test   //1805443 1800849 1801046    1805238   多 1813931  4 source:'yunn' ,"source":"hunan"  0：1814337  2：1804911
    public void testNoticeDetail()throws Exception{
        String requestBody = "{\"type\":2}";
        String responseString = mockMvc.perform(post("/notice/detail/1804911").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                .header("X-TOKEN",token)
        ).andExpect(status().isOk())    //返回的状态是200
        .andDo(print())         //打印出请求和相应的内容
        .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-----返回的json = " + responseString);
    }

    @Test  //4629 1801046 1803392  1805702 1805702
    public void testQueryRelNotice()throws Exception{
        String requestBody = "{\"type\":2}";
        String responseString = mockMvc.perform(post("/notice/queryRelNotice/1805702").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                .header("X-TOKEN",token)
        ).andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testqueryNoticeFile()throws Exception{
//        String requestBody = "{\"type\":0}";
        String responseString = mockMvc.perform(post("/notice/queryNoticeFile/1801046").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
//                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
        ).andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-----返回的json = " + responseString);
    }

    @Test //1800849
    public void testqueryCompanyList()throws Exception{
        String requestBody = "{\"pageNo\":0,\"pageSize\":20}";
        String responseString = mockMvc.perform(post("/notice/queryCompanyList/1800849").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                .header("X-TOKEN",token)
        ).andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testNoticeService()throws Exception {
        Page page = new Page();
        page.setPageSize(20);
        page.setCurrentPage(1);

        Map param=new HashMap();
        param.put("type","0");
        noticeService.queryNoticeList(page,param);
    }

    @Test
    public void testFile() throws Exception {
        //@RequestParam(value = "data", required = false) List<MultipartFile> files
        MockMultipartFile firstFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some xml".getBytes());
        MockMultipartFile secondFile = new MockMultipartFile("data", "other-file-name.data", "text/plain", "some other type".getBytes());
        MockMultipartFile jsonFile = new MockMultipartFile("json", "", "application/json", "{\"json\": \"someValue\"}".getBytes());
        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/upload")
                .file(firstFile)
                .file(secondFile).file(jsonFile)
                .param("some-random", "4"))
                .andExpect(status().is(200));
//                .andExpect(content().string("success"));
    }

    @Test
    public void testMatchName()throws Exception{
        String requestBody = "{\"name\":\"耀邦\"}";
        String responseString = mockMvc.perform(post("/company/matchName").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                .header("X-TOKEN",token)
        ).andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-----返回的json = " + responseString);
    }

}
