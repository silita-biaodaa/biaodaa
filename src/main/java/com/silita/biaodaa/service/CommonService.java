package com.silita.biaodaa.service;

import com.alibaba.fastjson.JSONObject;
import com.silita.biaodaa.dao.CommonMapper;
import com.silita.biaodaa.model.TbUser;
import com.silita.biaodaa.utils.MyDateUtils;
import com.silita.biaodaa.utils.PropertiesUtils;
import com.silita.biaodaa.utils.SignConvertUtil;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * Created by dh on 2018/4/10.
 */
@Service
public class CommonService {
    @Autowired
    private CommonMapper commonMapper;

    public List<Map<String, Object>> queryPbMode(){
        return commonMapper.queryPbMode();
    }

    public List queryCertZzByCompanyName(String companyName){
        return commonMapper.queryCertZzByCompanyName(companyName);
    }

    //记录用户访问模块轨迹
    public void insertRecordLog(String type, String userId, String ipAddres) {
        Map argMap =new HashedMap();
        argMap.put("type",type);
        argMap.put("userId",userId);
        argMap.put("type",type);
        argMap.put("ip",ipAddres);
        argMap.put("date", MyDateUtils.getDate(null));
        int clickCount = commonMapper.queryClickByUserId(argMap);
        if (clickCount > 0) { // 存在点击则修改点击次数和最后一次点击时间
            commonMapper.updateUserClick(argMap);
        } else { // 不存在则新增一条
            commonMapper.insertUserClick(argMap);
        }
    }

    public TbUser isLogin(String name,String pwd){
        TbUser tbUser = null;
        if(!StringUtil.isEmpty(name)&&!StringUtil.isEmpty(pwd)){
            TbUser user = null; // TODO: 18/4/13 取数据库数据
            if(user!=null&&user.getPassword()!=null&&pwd.equals(user.getPassword())){
                //登录成功,设置X-TOKEN
                Map<String, String> parameters = new HashedMap();
                parameters.put("name", name);
                parameters.put("pwd", pwd);
                parameters.put("showName", user.getDisplayName());
                parameters.put("userId", user.getPkid().toString());
                try {
                    String secret = PropertiesUtils.getProperty("CONTENT_SECRET");
                    String sign = SignConvertUtil.generateMD5Sign(secret, parameters);
                    String parameterJson = JSONObject.toJSONString(parameters);
                    String asB64 = Base64.getEncoder().encodeToString(parameterJson.getBytes("utf-8"));
                    String xtoken = sign+"."+asB64;
                    user.setXtoken(xtoken);
                } catch(NoSuchAlgorithmException e) {
                    //logger.error(e.getMessage(), e);
                } catch(UnsupportedEncodingException e) {
                    //logger.error(e.getMessage(), e);
                }
                return user;
            }
        }
        return tbUser;
    }

}
