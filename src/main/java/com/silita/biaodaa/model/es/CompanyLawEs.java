package com.silita.biaodaa.model.es;

import com.silita.biaodaa.elastic.Annotation.Document;
import com.silita.biaodaa.elastic.Annotation.Filed;
import com.silita.biaodaa.elastic.Enum.FieldType;
import com.silita.biaodaa.elastic.model.ElasticEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Document(indexName = "biaodaa", type = "companyforlaw")
public class CompanyLawEs extends ElasticEntity {

    @Filed(type = FieldType.text, store = true)
    private String comId;

    @Filed(type = FieldType.keyword)
    private String comName;

    @Filed(type = FieldType.integer)
    private Integer briCount;

    @Filed(type = FieldType.integer)
    private Integer judCount;

    @Filed(type = FieldType.integer)
    private Integer total;

    public String getId() {
        return this.comId;
    }

    public void setId(String id) {
        id = comId;
    }
}
