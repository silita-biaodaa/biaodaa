package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by 91567 on 2018/4/12.
 */
@WebAppConfiguration
public class GmyServiceTest extends ConfigTest {

    protected MockMvc mockMvc;
    @Autowired
    protected WebApplicationContext wac;

    @Before()
    public void setup()
    {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();  //初始化MockMvc对象
    }


    @Test
    public void testController()throws Exception{
        String requestBody = "{\"type\":0,\"showType\":0}";
        String responseString = mockMvc.perform(get("/foundation/listBannerImage").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.getBytes())
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testController2()throws Exception{
        String requestBody = "{\"userId\":\"dba718748d3e4a2096c1c948b7d\",\"isSystem\":0,\"pageNo\":1,\"pageSize\":20}";
        String responseString = mockMvc.perform(post("/userCenter/listMessageByUserId").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.getBytes())
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testController3()throws Exception{
        String requestBody = "{\"type\":1}";
        String responseString = mockMvc.perform(post("/foundation/listHotHotWords").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.getBytes())
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testController4()throws Exception{
        String requestBody = "{\"invitationIp\":\"127.0.0.1\",\"invitationPhone\":\"18774987061\",\"type\":1}";
        String responseString = mockMvc.perform(post("/authorize/getVerificationCode").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.getBytes())
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testController6()throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "gmytest");
        jsonObject.put("userpass", "gmytest");
        jsonObject.put("userphone", "18774987061");
        jsonObject.put("invitationCode", "900007");
        jsonObject.put("version", "10100");
        jsonObject.put("loginchannel", "1002");
        String requestBody = jsonObject.toJSONString();
        String responseString = mockMvc.perform(post("/authorize/userRegister").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.getBytes())
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testController7()throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userphone", "18774987061");
        jsonObject.put("userpass", "gmytest");
        jsonObject.put("version", "10100");
        jsonObject.put("loginchannel", "1002");
        String requestBody = jsonObject.toJSONString();
        String responseString = mockMvc.perform(post("/authorize/userLogin").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.getBytes())
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testController8()throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", "18774987061");
        jsonObject.put("userpass", "gmytest");
        jsonObject.put("userphone", "18774987061");
        jsonObject.put("invitationCode", "900007");
        jsonObject.put("wxopenid", "ixixiixix");
        jsonObject.put("wxUnionid", "xixixixix");
        jsonObject.put("version", "10100");
        jsonObject.put("loginchannel", "1002");
//        jsonObject.put("qqopenid", "ixixiixix");
        jsonObject.put("type", "1");
        jsonObject.put("nikename", "test");
        String requestBody = jsonObject.toJSONString();
        String responseString = mockMvc.perform(post("/authorize/thirdPartyBindingOrRegister").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.getBytes())
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testController9()throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userid", "67edf8831ddf49008cf3161e1b92b743");
        jsonObject.put("imgurl", "test.img");
        jsonObject.put("nickname", "update");
        jsonObject.put("gender", "1");
        jsonObject.put("mailbox", "999999@qq.com");
        jsonObject.put("companyname", "测试");
        String requestBody = jsonObject.toJSONString();
        String responseString = mockMvc.perform(post("/userCenter/updateUserTemp").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.getBytes())
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testController10()throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userpass", "123321");
        jsonObject.put("userid", "67edf8831ddf49008cf3161e1b92b743");
        jsonObject.put("userphone", "18774987061");
        jsonObject.put("invitationCode", "900007");
        jsonObject.put("version", "10100");
        jsonObject.put("loginchannel", "1002");

        String requestBody = jsonObject.toJSONString();
        String responseString = mockMvc.perform(post("/userCenter/updatePassWord").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.getBytes())
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testController11()throws Exception{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userpass", "123321");
        jsonObject.put("userid", "67edf8831ddf49008cf3161e1b92b743");
        jsonObject.put("userphone", "18774987061");
        jsonObject.put("invitationCode", "900007");
        jsonObject.put("version", "10100");
        jsonObject.put("loginchannel", "1002");

        String requestBody = jsonObject.toJSONString();
        String responseString = mockMvc.perform(post("/userCenter/updatePassWord").characterEncoding("UTF-8")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody.getBytes())
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("-----返回的json = " + responseString);
    }

    @Test
    public void testController12()throws Exception{
        MockMultipartFile firstFile = new MockMultipartFile("files", "filename.txt", "text/plain", "some xml".getBytes());

        String responseString = mockMvc.perform(fileUpload("/userCenter/updateHeadPortrait").file(firstFile)
        )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn().getResponse().getContentAsString();
        System.out.println("-----返回的json = " + responseString);
    }
}
