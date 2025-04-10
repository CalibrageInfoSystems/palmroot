package com.calibrage.palmroot.dbmodels;

public class ConsignmentData {

    private int Id;
    private  String ConsignmentCode;
    private String Originname;
    private String Vendorname;
    private String Varietyname;
    private  int EstimatedQuantity;
    private String CreatedDate;
    private String ArrivedDate;
    private int ArrivedQuantity;
    private String Status;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
    //    private boolean isSelected = false;

//    public boolean isSelected() {
//        return isSelected;
//    }
//
//    public void setSelected(boolean selected) {
//        isSelected = selected;
//    }

    private boolean isChecked = false;


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getEstimatedQuantity() {
        return EstimatedQuantity;
    }

    public void setEstimatedQuantity(int estimatedQuantity) {
        EstimatedQuantity = estimatedQuantity;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getArrivedDate() {
        return ArrivedDate;
    }

    public void setArrivedDate(String arrivedDate) {
        ArrivedDate = arrivedDate;
    }

    public int getArrivedQuantity() {
        return ArrivedQuantity;
    }

    public void setArrivedQuantity(int arrivedQuantity) {
        ArrivedQuantity = arrivedQuantity;
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
}
