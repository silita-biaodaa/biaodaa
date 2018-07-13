package com.silita.biaodaa.model;

import com.silita.biaodaa.elastic.Annotation.Document;
import com.silita.biaodaa.elastic.Annotation.Filed;
import com.silita.biaodaa.elastic.Enum.FieldType;
import com.silita.biaodaa.elastic.model.ElasticEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(indexName = "company", type = "comes")
public class CompanyEs extends ElasticEntity {

    @Filed(type = FieldType.integer, store = true)
    private Integer comId;

    @Filed(type = FieldType.keyword)
    private String comName;

    @Filed(type = FieldType.keyword)
    private String comNamePy;

    @Filed(type = FieldType.integer)
    private String px;

    public String getId() {
        return this.comId.toString();
    }

    public void setId(String id) {
        id = comId.toString();
    }
}
