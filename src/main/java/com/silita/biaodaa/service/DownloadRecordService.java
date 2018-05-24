package com.silita.biaodaa.service;

import com.silita.biaodaa.dao.TbDownloadRecordMapper;
import com.silita.biaodaa.model.TbDownloadRecord;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DownloadRecordService {

    @Autowired
    TbDownloadRecordMapper tbDownloadRecordMapper;

    public Integer downloadRecord(Map<String,Object> param){
        Integer record = 0;
        int count = 0;
        TbDownloadRecord tbDownloadRecord = new TbDownloadRecord();
        TbDownloadRecord downloadRecord = tbDownloadRecordMapper.queryDownloadRecord(param);
        if(null != downloadRecord){
            record = downloadRecord.getRecord();
            record+=1;
            tbDownloadRecord.setPkid(downloadRecord.getPkid());
            tbDownloadRecord.setRecord(record);
            tbDownloadRecord.setUpdated(new Date());
            count = tbDownloadRecordMapper.updateDownloadRecord(tbDownloadRecord);
        }else {
            record+=1;
            tbDownloadRecord.setRecord(record);
            tbDownloadRecord.setDownType(MapUtils.getString(param,"downType"));
            tbDownloadRecord.setFileId(MapUtils.getInteger(param,"fileId"));
            tbDownloadRecord.setUrl(MapUtils.getString(param,"url"));
            tbDownloadRecord.setCreated(new Date());
            count = tbDownloadRecordMapper.insertDownloadRecord(tbDownloadRecord);
        }
        if(count > 0){
            return record;
        }
        return 0;
    }

}
