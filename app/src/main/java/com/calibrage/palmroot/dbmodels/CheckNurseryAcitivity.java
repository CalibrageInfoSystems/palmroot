package com.calibrage.palmroot.dbmodels;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckNurseryAcitivity {

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("ActivityTypeId")
    @Expose
    private Integer activityTypeId;
    @SerializedName("Code")
    @Expose
    private String code;

    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("TargetDays")
    @Expose
    private Integer targetDays;

    @SerializedName("IsMultipleEntries")
    @Expose
    private String IsMultipleEntries;

    @SerializedName("ActicityType")
    @Expose
    private String ActicityType;

    public String getActicityType() {
        return ActicityType;
    }

    public void setActicityType(String acticityType) {
        ActicityType = acticityType;
    }

    @SerializedName("IsActive")
    @Expose
    private int isActive;
    @SerializedName("CreatedByUserId")
    @Expose
    private Integer createdByUserId;
    @SerializedName("CreatedDate")
    @Expose
    private String createdDate;
    @SerializedName("UpdatedByUserId")
    @Expose
    private Integer updatedByUserId;
    @SerializedName("UpdatedDate")
    @Expose
    private String updatedDate;
    @SerializedName("ServerUpdatedStatus")
    @Expose
    private int serverUpdatedStatus;

    @SerializedName("StatusTypeId")
    @Expose
    private int StatusTypeId;

    @SerializedName("Desc")
    @Expose
    private String Desc;

    public String getDependentActivityCode() {
        return DependentActivityCode;
    }

    public void setDependentActivityCode(String dependentActivityCode) {
        DependentActivityCode = dependentActivityCode;
    }

    @SerializedName("ConsignmentCode")
    @Expose
    private String ConsignmentCode;
    @SerializedName("DependentActivityCode")
    @Expose
    private String DependentActivityCode;
    @SerializedName("UpdatedDate")
    @Expose
    private String UpdatedDate;
    @SerializedName("ActivityDoneDate")
    @Expose
    private String ActivityDoneDate;

    @SerializedName("TargetDate")
    @Expose
    private String TargetDate;

    public String getTargetDate() {
        return TargetDate;
    }

    public void setTargetDate(String targetDate) {
        TargetDate = targetDate;
    }

    public String getActivityDoneDate() {
        return ActivityDoneDate;
    }

    public void setActivityDoneDate(String activityDoneDate) {
        ActivityDoneDate = activityDoneDate;
    }

    public String getConsignmentCode() {
        return ConsignmentCode;
    }

    public void setConsignmentCode(String consignmentCode) {
        ConsignmentCode = consignmentCode;
    }

    public int getStatusTypeId() {
        return StatusTypeId;
    }

    public void setStatusTypeId(int statusTypeId) {
        StatusTypeId = statusTypeId;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getIsMultipleEntries() {
        return IsMultipleEntries;
    }

    public void setIsMultipleEntries(String isMultipleEntries) {
        IsMultipleEntries = isMultipleEntries;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(Integer activityTypeId) {
        this.activityTypeId = activityTypeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTargetDays() {
        return targetDays;
    }

    public void setTargetDays(Integer targetDays) {
        this.targetDays = targetDays;
    }

    public int getIsActive() {
        return isActive;
    }

    public void setIsActive(int isActive) {
        this.isActive = isActive;
    }

    public Integer getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Integer createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Integer getUpdatedByUserId() {
        return updatedByUserId;
    }

    public void setUpdatedByUserId(Integer updatedByUserId) {
        this.updatedByUserId = updatedByUserId;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public int getServerUpdatedStatus() {
        return serverUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        this.serverUpdatedStatus = serverUpdatedStatus;
    }
}
