package com.calibrage.palmroot.dbmodels;

public class SaplingActivityStatusModel {

 //private Integer Id;
    private String ConsignmentCode;
    private Integer ActivityId;
    private int StatusTypeId;
    private int CreatedByUserId;
    private String CreatedDate;
    private int UpdatedByUserId;
    private String UpdatedDate;
    private int ServerUpdatedStatus;
    private String JobCompletedDate;

    public String getJobCompletedDate() {
        return JobCompletedDate;
    }

    public void setJobCompletedDate(String jobCompletedDate) {
        JobCompletedDate = jobCompletedDate;
    }

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

//    public Integer getId() {
//        return Id;
//    }
//
//    public void setId(Integer id) {
//        Id = id;
//    }

    public String getConsignmentCode() {
        return ConsignmentCode;
    }

    public void setConsignmentCode(String consignmentCode) {
        ConsignmentCode = consignmentCode;
    }

    public Integer getActivityId() {
        return ActivityId;
    }

    public void setActivityId(Integer activityId) {
        ActivityId = activityId;
    }

    public int getStatusTypeId() {
        return StatusTypeId;
    }

    public void setStatusTypeId(int statusTypeId) {
        StatusTypeId = statusTypeId;
    }

    public int getCreatedByUserId() {
        return CreatedByUserId;
    }

    public void setCreatedByUserId(int createdByUserId) {
        CreatedByUserId = createdByUserId;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public int getUpdatedByUserId() {
        return UpdatedByUserId;
    }

    public void setUpdatedByUserId(int updatedByUserId) {
        UpdatedByUserId = updatedByUserId;
    }

    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        UpdatedDate = updatedDate;
    }
}
