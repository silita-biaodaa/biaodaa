package com.silita.biaodaa.service;

import com.silita.biaodaa.common.VisitInfoHolder;
import com.silita.biaodaa.dao.TbDownloadRecordMapper;
import com.silita.biaodaa.model.TbDownloadRecord;
import com.silita.biaodaa.utils.MyDateUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DownloadRecordService {

    @Autowired
    TbDownloadRecordMapper tbDownloadRecordMapper;

    public Integer downloadRecord(Map<String, Object> param) {
        Integer record = 0;
        int count = 0;
        TbDownloadRecord tbDownloadRecord = new TbDownloadRecord();
        TbDownloadRecord downloadRecord = tbDownloadRecordMapper.queryDownloadRecord(param);
        if (null != downloadRecord) {
            record = downloadRecord.getRecord();
            record += 1;
            tbDownloadRecord.setPkid(downloadRecord.getPkid());
            tbDownloadRecord.setRecord(record);
            tbDownloadRecord.setUpdated(new Date());
            count = tbDownloadRecordMapper.updateDownloadRecord(tbDownloadRecord);
        } else {
            param.put("record", record);
            tbDownloadRecord = initDownRecord(param, "add");
            count = tbDownloadRecordMapper.insertDownloadRecord(tbDownloadRecord);
        }
        if (count > 0) {
            return record;
        }
        return 0;
    }

    public int getCompanyRecord(Map<String, Object> param) {
        param.put("fileId", VisitInfoHolder.getUserId());
        String toDay = MyDateUtils.getDate(null);
        param.put("downDate", toDay);
        Integer record = 0;
        int count = 0;
        TbDownloadRecord tbDownloadRecord = new TbDownloadRecord();
        TbDownloadRecord downloadRecord = tbDownloadRecordMapper.queryDownloadRecord(param);
        if (null != downloadRecord) {
            record = downloadRecord.getRecord();
            if (record > 50) {
                return record;
            }
            record += 1;
            tbDownloadRecord.setPkid(downloadRecord.getPkid());
            tbDownloadRecord.setRecord(record);
            tbDownloadRecord.setUpdated(new Date());
            count = tbDownloadRecordMapper.updateDownloadRecord(tbDownloadRecord);
        } else {
            record += 1;
            param.put("record", record);
            tbDownloadRecord = initDownRecord(param, "add");
            count = tbDownloadRecordMapper.insertDownloadRecord(tbDownloadRecord);
        }
        if (count > 0) {
            return record;
        }
        return 0;
    }

    /**
     * 初始化参数值
     *
     * @param param
     * @return
     */
    private TbDownloadRecord initDownRecord(Map<String, Object> param, String operate) {
        TbDownloadRecord tbDownloadRecord = new TbDownloadRecord();
        tbDownloadRecord.setRecord(MapUtils.getInteger(param, "record"));
        tbDownloadRecord.setDownType(MapUtils.getString(param, "downType"));
        tbDownloadRecord.setFileId(MapUtils.getString(param, "fileId"));
        tbDownloadRecord.setUrl(MapUtils.getString(param, "url"));
        tbDownloadRecord.setDownDate(MapUtils.getString(param, "downDate"));
        if ("add".equals(operate)) {
            tbDownloadRecord.setCreated(new Date());
        } else {
            tbDownloadRecord.setUpdated(new Date());
        }
        return tbDownloadRecord;
    }
}
