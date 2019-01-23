package membership;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.model.TbVipFeeStandard;
import com.silita.biaodaa.service.CommonService;
import com.silita.biaodaa.service.ConfigTest;
import com.silita.biaodaa.service.NoticeService;
import com.silita.biaodaa.to.ToOpenMember;
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
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();  //初始化MockMvc对象 //.addFilter(new AspectCheckLogin(),"/*")
        try {
//            memberLogin();
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
        String requestBody = "{\"loginName\":\"\",\"loginPwd\":\"123456\",\"phoneNo\":\"13319555802\",\"channel\":\"1002\",\"clientVersion\":\"10611\"}";
        requestBody = "{\"loginName\":\"carefee\",\"loginPwd\":\"8428cd0116f3625394bc2880f6b0689b8f70a9e7\",\"phoneNo\":\"15580062552\",\"channel\":\"1001\",\"clientVersion\":\"10611\"}";
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
        String localtoken = null;
        if(data != null) {
            localtoken = data.getString("xtoken");
            System.out.println(localtoken);
            token = localtoken;
        }
    }

    @Test
    public void memberRegister()throws Exception{ //714241
        String requestBody = "{\"inviterCode\":\"test12\",\"verifyCode\":\"111259\",\"loginName\":\"\",\"loginPwd\":\"7c222fb2927d828af22f592134e8932480637c0d\",\"phoneNo\":\"13319555802\",\"channel\":\"1002\",\"clientVersion\":\"22222\"}";
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
//        token = "MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiJkYWlodWFuIiwibG9naW5fdGltZSI6IjE1NDY1ODc1NjgyOTMiLCJwaG9uZV9ubyI6IjEzMzE5NTU1ODAxIiwicGtpZCI6IjE1NDY0MTcyMzkiLCJ0b2tlblZlcnNpb24iOiIyMDE5MDEwMyJ9.631C870DD1ABA350560FA2F09290EF1E";
//        token ="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMSIsImxvZ2luX25hbWUiOiJkYWlodWFuIiwibG9naW5fdGltZSI6IjE1NDY1ODk3MzIzNDciLCJwaG9uZV9ubyI6IjEzMzE5NTU1ODAxIiwicGtpZCI6IjE1NDY0MTcyMzkiLCJ0b2tlblZlcnNpb24iOiIyMDE5MDEwMyJ9.580BA8B979321B151DE890450C1F81FD";
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

    @Test
    public void testUpdateUserInfo()throws Exception {
        token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luVGltZSI6IjE1NDc3ODA2Mzc1MTEiLCJwZXJtaXNzaW9ucyI6ImJpZF9maWx0ZXIiLCJwaG9uZU5vIjoiMTMzMTk1NTU4MDIiLCJwa2lkIjoiODk3ZDg0OTcxOTZkNDEzNWIwZGVjMmI4MmJhNmY0YTIiLCJyb2xlQ29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIn0=.5BE6C3353FE092F265DDC2F782979302";
        String requestBody = "{\"sex\":\"0\"}";  //,"":"" 3AUM6L  test12  1AUM6L
        requestBody = "{\"inviterCode\":\"3AUM6L\",\"loginName\":\"login_tes2222\",\"userName\":\"user_name222\"" +
                ",\"sex\":\"1\",\"nikeName\":\"曹fb22\",\"email\":\"222caoliang@fb.com\"" +
                ",\"birthYear\":\"2018-10-08\",\"inCity\":\"城市2\",\"inCompany\":\"逗比公司2\"" +
                ",\"position\":\"逗比总监22\",\"imageUrl\":\"http://2222sdlki.com\"}";
        String responseString = mockMvc.perform(post("/userCenter/updateUserInfo").characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                        .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                .header("X-TOKEN",token)
        )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testPermssion()throws Exception{
        token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luTmFtZSI6ImxvZ2luX3RlczIyMjIiLCJsb2dpblRpbWUiOiIxNTQ3NDMwNzk4ODQxIiwicGVybWlzc2lvbnMiOiJiaWRfZmlsdGVyIiwicGhvbmVObyI6IjEzMzE5NTU1ODAyIiwicGtpZCI6IjYzZDQ1YjdiMjkzNTQwZjg4MTJkZjEyMzliMTI2ZjViIiwicm9sZUNvZGUiOiJub3JtYWwiLCJ0b2tlblZlcnNpb24iOiIyMDE5MDEwMyIsInVzZXJOYW1lIjoidXNlcl9uYW1lMjIyIn0=.D39F5396CEA8A371814E8B5E113AC034";
        String requestBody = "{\"name\":\"123\"}";  //,"":"" /permission
        String responseString = mockMvc.perform(post("/permission/under/list").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                .header("X-TOKEN",token)
        )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-#####----返回的json = " + responseString);
    }


    @Test
    public void testUpdatePwd()throws Exception{
//        token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luVGltZSI6IjE1NDc0NDYyMzMzNjkiLCJwZXJtaXNzaW9ucyI6ImJpZF9maWx0ZXIiLCJwaG9uZU5vIjoiMTMzMTk1NTU4MDIiLCJwa2lkIjoiODk3ZDg0OTcxOTZkNDEzNWIwZGVjMmI4MmJhNmY0YTIiLCJyb2xlQ29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIn0=.EDE5E5A2BE7D0B0A21AA2D77A8DADCC5";
        String requestBody = "{\"loginPwd\":\"123456\",\"verifyCode\":\"111259\",\"phoneNo\":\"13319555802\",\"channel\":\"1003\"}";  //,"":"" /permission
        String responseString = mockMvc.perform(post("/userCenter/updatePwd").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
//                .header("X-TOKEN",token)
        )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-#####----返回的json = " + responseString);
    }

    @Test
    public void testThirdPartyBinding()throws Exception{
//        token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luVGltZSI6IjE1NDc0NDYyMzMzNjkiLCJwZXJtaXNzaW9ucyI6ImJpZF9maWx0ZXIiLCJwaG9uZU5vIjoiMTMzMTk1NTU4MDIiLCJwa2lkIjoiODk3ZDg0OTcxOTZkNDEzNWIwZGVjMmI4MmJhNmY0YTIiLCJyb2xlQ29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIn0=.EDE5E5A2BE7D0B0A21AA2D77A8DADCC5";
        String requestBody = "{\"inviterCode\":\"3AUM6L\",\"imageUrl\":\"http://123k123.com\",\"wxUnionId\":\"oT8cu0oOMNauxx4e4q9UMM6BL123\",\"qqOpenId\":\"\",\"nikeName\":\"曹123\",\"wxOpenId\":\"oT8cu0oOMNauxx4e4q9UMM6BL222\",\"loginPwd\":\"123456\",\"verifyCode\":\"111259\",\"phoneNo\":\"13319555802\",\"channel\":\"1002\"}";  //,"":"" /permission
        String responseString = mockMvc.perform(post("/authorize/thirdPartyBinding").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
//                .header("X-TOKEN",token)
        )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-#####----返回的json = " + responseString);
    }


    @Test
    public void testQueryFeeStandard()throws Exception{
        token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luVGltZSI6IjE1NDc2MDkwOTk5NTQiLCJwZXJtaXNzaW9ucyI6ImJpZF9maWx0ZXIiLCJwaG9uZU5vIjoiMTMzMTk1NTU4MDIiLCJwa2lkIjoiODk3ZDg0OTcxOTZkNDEzNWIwZGVjMmI4MmJhNmY0YTIiLCJyb2xlQ29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIn0=.DBC514D8D21596A38F2049C9FECC17CB";
        String requestBody = "{\"channel\":\"1002\"}";
        String responseString = mockMvc.perform(post("/vip/queryFeeStandard").characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                        .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                .header("X-TOKEN",token)
        )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-#####----返回的json = " + responseString);
    }

    @Test
    public void testMemberThirdLogin()throws Exception{
//        token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luVGltZSI6IjE1NDc2MDkwOTk5NTQiLCJwZXJtaXNzaW9ucyI6ImJpZF9maWx0ZXIiLCJwaG9uZU5vIjoiMTMzMTk1NTU4MDIiLCJwa2lkIjoiODk3ZDg0OTcxOTZkNDEzNWIwZGVjMmI4MmJhNmY0YTIiLCJyb2xlQ29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIn0=.DBC514D8D21596A38F2049C9FECC17CB";
        String requestBody = "{\"wxOpenId\":\"\",\"wxUnionId\":\"\",\"qqOpenId\":\"\",\"channel\":\"1002\"}";
        String responseString = mockMvc.perform(post("/authorize/memberThirdLogin").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
//                .header("X-TOKEN",token)
        )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-#####----返回的json = " + responseString);
    }

    @Test
    public void testQueryMyProfits()throws Exception{
        token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luVGltZSI6IjE1NDc3ODA2Mzc1MTEiLCJwZXJtaXNzaW9ucyI6ImJpZF9maWx0ZXIiLCJwaG9uZU5vIjoiMTMzMTk1NTU4MDIiLCJwa2lkIjoiODk3ZDg0OTcxOTZkNDEzNWIwZGVjMmI4MmJhNmY0YTIiLCJyb2xlQ29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIn0=.5BE6C3353FE092F265DDC2F782979302";
        String requestBody = "{\"pageNo\":2,\"pageSize\":8}";
        String responseString = mockMvc.perform(post("/vip/queryMyProfits").characterEncoding("UTF-8")
                        .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                        .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                .header("X-TOKEN",token)
        )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-#####----返回的json = " + responseString);
    }

    @Test
    public void testGetVerifyCode()throws Exception{ //getVerifyCode  getVerificationCode
//        token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luVGltZSI6IjE1NDc3ODA2Mzc1MTEiLCJwZXJtaXNzaW9ucyI6ImJpZF9maWx0ZXIiLCJwaG9uZU5vIjoiMTMzMTk1NTU4MDIiLCJwa2lkIjoiODk3ZDg0OTcxOTZkNDEzNWIwZGVjMmI4MmJhNmY0YTIiLCJyb2xlQ29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIn0=.5BE6C3353FE092F265DDC2F782979302";
        String requestBody = "{\"type\":3,\"invitationPhone\":13319555801}";
        String responseString = mockMvc.perform(post("/authorize/getVerifyCode").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
//                .header("X-TOKEN",token)
        )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-#####----返回的json = " + responseString);
    }

    @Test
    public void testOpenMemberRights()throws Exception{
        token="MjAxOTAxMDM=.eyJjaGFubmVsIjoiMTAwMiIsImxvZ2luVGltZSI6IjE1NDc3ODA2Mzc1MTEiLCJwZXJtaXNzaW9ucyI6ImJpZF9maWx0ZXIiLCJwaG9uZU5vIjoiMTMzMTk1NTU4MDIiLCJwa2lkIjoiODk3ZDg0OTcxOTZkNDEzNWIwZGVjMmI4MmJhNmY0YTIiLCJyb2xlQ29kZSI6Im5vcm1hbCIsInRva2VuVmVyc2lvbiI6IjIwMTkwMTAzIn0=.5BE6C3353FE092F265DDC2F782979302";
        token="MjAxOTAxMDM=.eyJsb2dpbk5hbWUiOiJjYXJlZmVlIiwibG9naW5UaW1lIjoiMTU0ODIyNTEwODYyOSIsInBlcm1pc3Npb25zIjoiYmlkX2ZpbHRlcix0ZW5kZXJfZmlsdGVyIiwicGhvbmVObyI6IjE1NTgwMDYyNTUyIiwicGtpZCI6IjE1NDY0MTYzNjMiLCJyb2xlQ29kZSI6InN5c19tYW5nZXIiLCJ0b2tlblZlcnNpb24iOiIyMDE5MDEwMyJ9.1D0400A9EB8BDFE65867C456B3604EA5";
        ToOpenMember toOpenMember= new ToOpenMember();
        toOpenMember.setChannel("1002");
        TbVipFeeStandard tbVipFeeStandard = new TbVipFeeStandard();
        tbVipFeeStandard.setStdCode("quarter");
        toOpenMember.setFeeStandard(tbVipFeeStandard);

        String requestBody = "{\"pageNo\":2,\"pageSize\":8}";
        requestBody = JSONObject.toJSONString(toOpenMember);
        System.out.println("requestBody:"+requestBody);
        String responseString = mockMvc.perform(post("/vip/openMemberRights").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)// contentType(MediaType.APPLICATION_FORM_URLENCODED)//ajax格式 //添加参数(可以添加多个)
                .content(requestBody.getBytes())//.param("id","3")   //添加参数(可以添加多个)
                .header("X-TOKEN",token)
        )
                .andExpect(status().isOk())    //返回的状态是200
                .andDo(print())         //打印出请求和相应的内容
                .andReturn().getResponse().getContentAsString();   //将相应的数据转换为字符串
        System.out.println("-#####----返回的json = " + responseString);
    }
}
