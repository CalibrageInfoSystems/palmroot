package com.calibrage.palmroot.dbmodels;

public class ConsignmentReports {
    private  int Id;
    private  String NurseryCode;
    private  String NurseryName;
    private String ConsignmentCode;
    private String SAPCode;
    private String Originname;
    private String Vendorname;
    private String Varietyname;
    private  String SowingDate;

    public float getArrivalquantity() {
        return ArrivedQuantity;
    }

    public void setArrivalquantity(float arrivalquantity) {
        this.ArrivedQuantity = arrivalquantity;
    }

    private float ArrivedQuantity;
    private String TransplantingDate;
    private int  CurrentClosingStock;
    private String Status;
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNurseryCode() {
        return NurseryCode;
    }

    public void setNurseryCode(String nurseryCode) {
        NurseryCode = nurseryCode;
    }

    public String getNurseryName() {
        return NurseryName;
    }

    public void setNurseryName(String nurseryName) {
        NurseryName = nurseryName;
    }

    public String getConsignmentCode() {
        return ConsignmentCode;
    }

    public void setConsignmentCode(String consignmentCode) {
        ConsignmentCode = consignmentCode;
    }

    public String getSAPId() {
        return SAPCode;
    }

    public void setSAPId(String SAPId) {
        this.SAPCode = SAPId;
    }

    public String getOriginname() {
        return Originname;
    }

    public void setOriginname(String originname) {
        Originname = originname;
    }

    public String getVendorname() {
        return Vendorname;
    }

    public void setVendorname(String vendorname) {
        Vendorname = vendorname;
    }

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

//    public int getSowingquantity() {
//        return Sowingquantity;
//    }
//
//    public void setSowingquantity(int sowingquantity) {
//        Sowingquantity = sowingquantity;
//    }

    public String getTransplantDate() {
        return TransplantingDate;
    }

    public void setTransplantDate(String transplantDate) {
        TransplantingDate = transplantDate;
    }

    public int getClosingStock() {
        return CurrentClosingStock;
    }

    public void setClosingStock(int closingStock) {
        CurrentClosingStock = closingStock;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }



}
