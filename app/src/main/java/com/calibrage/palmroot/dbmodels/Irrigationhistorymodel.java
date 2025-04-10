package com.calibrage.palmroot.dbmodels;

public class Irrigationhistorymodel {

    private Integer Id;
    private String IrrigationCode;
    private Integer StatusTypeId;
    private String Comments;
    private int isActive;
    private int CreatedByUserId;
    private String CreatedDate;
    private int ServerUpdatedStatus;


    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getIrrigationCode() {
        return IrrigationCode;
    }

    public void setIrrigationCode(String irrigationCode) {
        IrrigationCode = irrigationCode;
    }

    public Integer getStatusTypeId() {
        return StatusTypeId;
    }

    public void setStatusTypeId(Integer statusTypeId) {
        StatusTypeId = statusTypeId;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
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

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }



}
