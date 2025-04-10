package com.calibrage.palmroot.dbmodels;

public class Visitdata {
    private String VisitedBy;
    private String ConsignmentCode;
    private String Remarkes;
    private  String UpdatedBy;

    public String getVisitedBy() {
        return VisitedBy;
    }

    public void setVisitedBy(String visitedBy) {
        VisitedBy = visitedBy;
    }

    public String getConsignmentCode() {
        return ConsignmentCode;
    }

    public void setConsignmentCode(String consignmentCode) {
        ConsignmentCode = consignmentCode;
    }

    public String getRemarkes() {
        return Remarkes;
    }

    public void setRemarkes(String remarkes) {
        Remarkes = remarkes;
    }

    public String getUpdatedBy() {
        return UpdatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        UpdatedBy = updatedBy;
    }
}
