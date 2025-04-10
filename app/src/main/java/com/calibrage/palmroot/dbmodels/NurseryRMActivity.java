package com.calibrage.palmroot.dbmodels;

public class NurseryRMActivity {

    private Integer TypeCdId,ClassTypeId;
    private  String Desc,TableName;

    public Integer getTypeCdId() {
        return TypeCdId;
    }

    public void setTypeCdId(Integer typeCdId) {
        TypeCdId = typeCdId;
    }

    public Integer getClassTypeId() {
        return ClassTypeId;
    }

    public void setClassTypeId(Integer classTypeId) {
        ClassTypeId = classTypeId;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getTableName() {
        return TableName;
    }

    public void setTableName(String tableName) {
        TableName = tableName;
    }
}
