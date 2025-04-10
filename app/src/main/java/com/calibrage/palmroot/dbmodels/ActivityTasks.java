package com.calibrage.palmroot.dbmodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivityTasks {

    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("ActivityTypeId")
    @Expose
    private Integer activityTypeId;
    @SerializedName("Dependency")
    @Expose
    private String dependency;
    @SerializedName("IsOptional")
    @Expose
    private Integer isOptional;
    @SerializedName("Bucket")
    @Expose
    private String bucket;
    @SerializedName("Field")
    @Expose
    private String field;
    @SerializedName("ItemCode")
    @Expose
    private String itemCode;
    @SerializedName("ItemCodeName")
    @Expose
    private String itemCodeName;
    @SerializedName("GLCode")
    @Expose
    private String gLCode;
    @SerializedName("GLName")
    @Expose
    private String gLName;
    @SerializedName("CostCenter")
    @Expose
    private String costCenter;
    @SerializedName("InputType")
    @Expose
    private String inputType;
    @SerializedName("UOM")
    @Expose
    private String uom;
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

    @SerializedName("DataType")
    @Expose
    private String DataType;

    @SerializedName("GroupId")
    @Expose
    private Integer GroupId;
    @SerializedName("SortOrder")
    @Expose
    private Integer SortOrder;

    public String getDataType() {
        return DataType;
    }

    public void setDataType(String dataType) {
        DataType = dataType;
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

    public String getDependency() {
        return dependency;
    }

    public void setDependency(String dependency) {
        this.dependency = dependency;
    }

    public Integer getIsOptional() {
        return isOptional;
    }

    public void setIsOptional(Integer isOptional) {
        this.isOptional = isOptional;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemCodeName() {
        return itemCodeName;
    }

    public void setItemCodeName(String itemCodeName) {
        this.itemCodeName = itemCodeName;
    }

    public String getGLCode() {
        return gLCode;
    }

    public void setGLCode(String gLCode) {
        this.gLCode = gLCode;
    }

    public String getGLName() {
        return gLName;
    }

    public void setGLName(String gLName) {
        this.gLName = gLName;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
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

    public Integer getGroupId() {
        return GroupId;
    }

    public void setGroupId(Integer groupId) {
        GroupId = groupId;
    }

    public Integer getSortOrder() {
        return SortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        SortOrder = sortOrder;
    }
}
