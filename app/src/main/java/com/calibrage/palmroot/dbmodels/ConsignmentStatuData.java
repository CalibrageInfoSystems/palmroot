package com.calibrage.palmroot.dbmodels;

public class ConsignmentStatuData {

    private  String SowingDate;
    private String CreatedDate;
    private String ConsignmentCode;
    private String Originname;
    private String StatusType;
    private String Varietyname;


    public String getVarietyname() {
        return Varietyname;
    }

    public void setVarietyname(String varietyname) {
        Varietyname = varietyname;
    }

    public String getSowingDate() {
        return SowingDate;
    }

    public void setSowingDate(String sowingDate) {
        SowingDate = sowingDate;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getConsignmentCode() {
        return ConsignmentCode;
    }

    public void setConsignmentCode(String consignmentCode) {
        ConsignmentCode = consignmentCode;
    }

    public String getOriginname() {
        return Originname;
    }

    public void setOriginname(String originname) {
        Originname = originname;
    }

    public String getStatusType() {
        return StatusType;
    }

    public void setStatusType(String statusType) {
        StatusType = statusType;
    }
}
