package com.silita.biaodaa.model.es;

import com.silita.biaodaa.elastic.Annotation.Document;
import com.silita.biaodaa.elastic.Annotation.Filed;
import com.silita.biaodaa.elastic.Enum.FieldType;
import com.silita.biaodaa.elastic.model.ElasticEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Document(indexName = "branch_company", type = "branch_comes")
public class BranchCompanyEs extends ElasticEntity {

    /**
     * 公司id
     */
    @Filed(type = FieldType.text, store = true)
    private String comId;

    /**
     * 分支机构集合
     */
    @Filed(type = FieldType.nested)
    private List<CompanyInfoEs> branchCompanys;

    public String getId() {
        return this.comId;
    }

    public void setId(String id) {
        id = comId;
    }
}
