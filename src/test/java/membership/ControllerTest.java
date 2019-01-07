package membership;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.common.CheckLoginFilter;
import com.silita.biaodaa.service.CommonService;
import com.silita.biaodaa.service.ConfigTest;
import com.silita.biaodaa.service.NoticeService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@TransactionConfiguration(transactionManager="txManager",defaultRollback=true)
@WebAppConfiguration
public class ControllerTest extends ConfigTest {

    @Autowired
    NoticeService noticeService;

    @Autowired
    private CommonService commonService;


    protected MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext wac;

    @Before //这个方法在每个方法执行之前都会执行一遍
    public void setup()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilter(new CheckLoginFilter(),"/*").build();  //初始化MockMvc对象
        try {
//            token = initToken();
        }catch (Exception e){
            logger.error(e,e);
        }
    }

    @Test
    public void testLogin() throws Exception{
//        initToken();
    }

    @Test
    public void memberLogin()throws Exception{
        String requestBody = "{\"login_name\":\"\",\"login_pwd\":\"7c222fb2927d828af22f592134e8932480637c0d\",\"phone_no\":\"13319555801\",\"channel\":\"1002\",\"clientVersion\":\"10611\"}";
        String responseString = mockMvc.perform(post("/authorize/memberLogin").characterEncoding("UTF-8")
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
    }

    @Test
    public void memberRegister()throws Exception{ //714241
        String requestBody = "{\"verifyCode\":\"714241\",\"login_name\":\"\",\"login_pwd\":\"7c222fb2927d828af22f592134e8932480637c0d\",\"phone_no\":\"13319555801\",\"channel\":\"1002\",\"clientVersion\":\"22222\"}";
        String responseString = mockMvc.perform(post("/authorize/memberRegister").characterEncoding("UTF-8")
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
    }


///
//    public void testBuildSing(){
//        String sign = null;
//
//        Map<String, String> parameters = new HashedMap();
//        parameters.put("login_name", sysUser.getLogin_name());
//        parameters.put("user_name", sysUser.getUser_name());
//        parameters.put("pkid", sysUser.getPkid());
//        parameters.put("channel", sysUser.getChannel());
//        parameters.put("phone_no", sysUser.getPhone_no());
//        parameters.put("login_time", String.valueOf(System.currentTimeMillis()));
//        parameters.put("tokenVersion",tokenVersion);
//    }

    private String initToken()throws Exception{
        System.out.println("旧登录接口。。。");
        String requestBody = "{\"username\":\"daihuan\",\"userpass\":\"7c222fb2927d828af22f592134e8932480637c0d\",\"userphone\":\"13319555801\",\"loginchannel\":\"1001\",\"version\":\"10611\"}";
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
    @Test
    public void testqueryList()throws Exception{
        token = "MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiJkYWlodWFuIiwibG9naW5fdGltZSI6IjE1NDY1ODc1NjgyOTMiLCJwaG9uZV9ubyI6IjEzMzE5NTU1ODAxIiwicGtpZCI6IjE1NDY0MTcyMzkiLCJ0b2tlblZlcnNpb24iOiIyMDE5MDEwMyJ9.631C870DD1ABA350560FA2F09290EF1E";
        token ="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiJkYWlodWFuIiwibG9naW5fdGltZSI6IjE1NDY1ODk3MzIzNDciLCJwaG9uZV9ubyI6IjEzMzE5NTU1ODAxIiwicGtpZCI6IjE1NDY0MTcyMzkiLCJ0b2tlblZlcnNpb24iOiIyMDE5MDEwMyJ9.580BA8B979321B151DE890450C1F81FD";
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


}
