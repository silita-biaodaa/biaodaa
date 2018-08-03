package com.silita.biaodaa.controller;

/**
 * 记录手机通讯录
 */

import com.silita.biaodaa.service.PhoneAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 记录手机号
 */
@RequestMapping("phone/address")
@RestController
public class PhoneAddressController {

    @Autowired
    PhoneAddressService phoneAddressService;

    /**
     * TODO: 添加手机号
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Map<String, Object> addPhoneAddress(String json) {
        return phoneAddressService.addPhoneAddress(json);
    }

}
