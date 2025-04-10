package com.calibrage.palmroot.dbmodels;

public class NurseryRMTransctions {

    private  String TransactionId;

    public int getActivityId() {
        return ActivityId;
    }

    public void setActivityId(int activityId) {
        ActivityId = activityId;
    }

    private int ActivityId;
    private int ActivityTypeId;
    private  int StatusTypeId;
    private String CreatedDate;
private String Desc;
private  String NurseryCode;

    public String getNurseryCode() {
        return NurseryCode;
    }

    public void setNurseryCode(String nurseryCode) {
        NurseryCode = nurseryCode;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }


    public int getActivityTypeId() {
        return ActivityTypeId;
    }

    public void setActivityTypeId(int activityTypeId) {
        ActivityTypeId = activityTypeId;
    }

    public int getStatusTypeId() {
        return StatusTypeId;
    }

    public void setStatusTypeId(int statusTypeId) {
        StatusTypeId = statusTypeId;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }


}


