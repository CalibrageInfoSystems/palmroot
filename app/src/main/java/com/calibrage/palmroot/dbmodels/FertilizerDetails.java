package com.calibrage.palmroot.dbmodels;

public class FertilizerDetails {
    private int ActivityId;
    private  String ActivityName;
    private  String Productname;

    public String getUOM() {
        return UOM;
    }

    public void setUOM(String UOM) {
        this.UOM = UOM;
    }

    private String UOM;
    private  String Quantity;
    private  String DoneBy;
    private  String DoneOn;

    public String getProductname() {
        return Productname;
    }

    public void setProductname(String productname) {
        Productname = productname;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getDoneBy() {
        return DoneBy;
    }

    public void setDoneBy(String doneBy) {
        DoneBy = doneBy;
    }

    public String getDoneOn() {
        return DoneOn;
    }

    public void setDoneOn(String doneOn) {
        DoneOn = doneOn;
    }

    public int getActivityId() {
        return ActivityId;
    }

    public void setActivityId(int activityId) {
        ActivityId = activityId;
    }

    public String getActivityName() {
        return ActivityName;
    }

    public void setActivityName(String activityName) {
        ActivityName = activityName;
    }



}
