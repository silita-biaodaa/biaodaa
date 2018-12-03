package com.silita.biaodaa.service;

import com.silita.biaodaa.utils.HttpUtils;
import com.silita.biaodaa.utils.JsonUtils;
import com.silita.biaodaa.utils.MD5Utils;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MD5Test {

    private String key = "b3b3a85e8f9e49cdfa3e3b09972d0648";
    private String uid = "A4104739";
    private String api = "A001";

    private String getSign(Map argMap) {
        try {
            String sign = MD5Utils.sign("uid=" + uid + "&api=" + api + "&args=" + JsonUtils.toJsonString(argMap) + "&key=" + key);
            return sign;
        } catch (Exception e) {
            return null;
        }
    }

    @Test
    public void http() throws UnsupportedEncodingException {
        Map argsMap = new HashMap();
        argsMap.put("entInfo", "湖南耀邦建设有限公司");
        String sign = getSign(argsMap);
        String requestUrl = "http://open.yscredit.com/api/request" + "?uid=" + uid + "&api=" + api + "&args=" + URLEncoder.encode(JsonUtils.toJsonString(argsMap), "UTF-8") + "&sign=" + sign;
        String result = HttpUtils.sendGet(requestUrl, null);
        Map resultMap = JsonUtils.json2Map(result);
        System.out.println(resultMap);
    }

    @Test
    public void http1() {
        String requestUrl = "http://web.hunanjz.com/action/personnel.ashx?opt=person&name=罗福求&sfz=430502196909253032";
        String result = HttpUtils.sendGet(requestUrl, null);
        Map resultMap = JsonUtils.json2Map(result);
        System.out.println(resultMap);
    }
}
