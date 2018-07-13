package com.silita.biaodaa.model;

import com.silita.biaodaa.elastic.Annotation.Document;
import com.silita.biaodaa.elastic.Annotation.Filed;
import com.silita.biaodaa.elastic.Enum.FieldType;
import com.silita.biaodaa.elastic.model.ElasticEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(indexName = "snatchurl", type = "snatchurl_zhaobiao")
public class SnatchurlEs extends ElasticEntity {

    @Filed(type = FieldType.integer,store = true)
    private Integer snatchId;

    @Filed(type = FieldType.keyword)
    private String title;

    public String getId() {
        return this.snatchId.toString();
    }

    public void setId(String id) {
        id = this.snatchId.toString();
    }
}
