package com.silita.biaodaa.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.silita.biaodaa.dao.TbPhoneAddressBookMapper;
import com.silita.biaodaa.model.TbPhoneAddressBook;
import com.silita.biaodaa.utils.*;
import org.apache.commons.collections.MapUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

import static org.apache.commons.collections.MapUtils.getString;

@Service
public class PhoneAddressService {

    @Autowired
    TbPhoneAddressBookMapper tbPhoneAddressBookMapper;

    public Map<String, Object> addPhoneAddress(String json) {
        Map<String, Object> param = JsonUtils.json2Map(json);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 0);
        resultMap.put("msg", "操作失败");
        String deviceId = null;
        //设备id
        if (null != param.get("deviceId")) {
            deviceId = getString(param, "deviceId");
        }
        if (MyStringUtils.isNull(deviceId)) {
            return resultMap;
        }
        List<Map<String, Object>> list = null;
        if (null != param.get("list")) {
            list = (List<Map<String, Object>>) param.get("list");
        }
        if (null == list || list.size() == 0) {
            return resultMap;
        }
        //登录手机号
        String userPhone = getString(param, "userPhone");
        //来源
        String sourceFrom = getString(param, "sourceFrom");
        int count = tbPhoneAddressBookMapper.queryPhoneAddressCount(deviceId);
        if (count > 0) {
            tbPhoneAddressBookMapper.delPhoneAddress(deviceId);
        }
        List<TbPhoneAddressBook> phoneAddressBookList = new ArrayList<>();
        TbPhoneAddressBook tbPhoneAddressBook = null;
        for (Map<String, Object> tbMap : list) {
            tbPhoneAddressBook = new TbPhoneAddressBook();
            tbPhoneAddressBook.setDeviceId(deviceId);
            tbPhoneAddressBook.setCreatedDate(new Date());
            tbPhoneAddressBook.setUserPhone(userPhone);
            if (MyStringUtils.isNotNull(getString(tbMap, "phoneName"))) {
                tbPhoneAddressBook.setPhoneName(EmojiUtils.emojiChange(getString(tbMap, "phoneName")));
            }
            tbPhoneAddressBook.setPhone(getString(tbMap, "phone"));
            tbPhoneAddressBook.setSourceFrom(sourceFrom);
            phoneAddressBookList.add(tbPhoneAddressBook);
        }
        if (null != phoneAddressBookList && phoneAddressBookList.size() > 0) {
            tbPhoneAddressBookMapper.batchInsertPhoneAddress(phoneAddressBookList);
        }
        resultMap.put("code", 1);
        resultMap.put("msg", "操作成功");
        return resultMap;
    }

    public List<Map<String, Object>> listPhone(Map<String, Object> param) {
        String comName = RegexUtils.setComName(MapUtils.getString(param, "name"));
        if (MyStringUtils.isNull(comName)) {
            return new ArrayList<>();
        }
        DBCollection dbCollection = MongodbUtils.init(PropertiesUtils.getProperty("mongodb.ip"),
                PropertiesUtils.getProperty("mongodb.port"), PropertiesUtils.getProperty("mongodb.dbName")).getDB().getCollection("phone");
        //模糊匹配
        Pattern pattern = Pattern.compile("^.*" + comName + ".*$", Pattern.CASE_INSENSITIVE);
        DBCursor dbCursor = dbCollection.find(new BasicDBObject("name", pattern));
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<Map<String, Object>> doweigtList = new ArrayList<>();
        Map<String, Object> map;
        if (null != dbCursor) {
            while (dbCursor.hasNext()) {
                map = dbCursor.next().toMap();
                map.remove("_id");
                mapList.add(map);
            }
        }
        if (null != mapList && mapList.size() > 0) {
            for (int i = 0; i < mapList.size(); i++) {
                for (int j = mapList.size() - 1; j >= i; j--) {
                    if (mapList.get(j).get("phone").toString().equals(mapList.get(i).get("phone"))) {
                        mapList.remove(j);
                    }
                }
            }
        }
        return mapList;
    }

    public HSSFWorkbook export(Map<String, Object> param) {
        List<Map<String, Object>> list = this.listPhone(param);
        if (null == list || list.size() <= 0) {
            return new HSSFWorkbook();
        }
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet();
        HSSFRow row = sheet.createRow(0);
        String[] heads = {"姓名", "手机号"};
        for (int i = 0; i < heads.length; i++) {
            row.createCell(i).setCellValue(heads[i]);
        }
        for (int j = 0; j < list.size(); j++) {
            HSSFRow row1 = sheet.createRow(j + 1);
            row1.createCell(0).setCellValue(MapUtils.getString(list.get(j), "name"));
            row1.createCell(1).setCellValue(MapUtils.getString(list.get(j), "phone"));
        }
        return hssfWorkbook;
    }
}
