package com.calibrage.palmroot.dbmodels;

public class NurseryAcitivity {
    int ActivityId,
            ActivityTypeId, ColorIndicator,  StatusTypeId;
    String IsMultipleEntries,
            ActicityType,
            ActivityCode,
            ActivityName,

            ActivityStatus,
            ActivityDoneDate,
            ConsignmentCode,
            TargetDate,
            Buffer1Date,
            Buffer2Date,
                    DependentActivityCode;

    public String getDependentActivityCode() {
        return DependentActivityCode;
    }

    public void setDependentActivityCode(String dependentActivityCode) {
        DependentActivityCode = dependentActivityCode;
    }

    public int getActivityId() {
        return ActivityId;
    }

    public void setActivityId(int activityId) {
        ActivityId = activityId;
    }

    public int getActivityTypeId() {
        return ActivityTypeId;
    }

    public void setActivityTypeId(int activityTypeId) {
        ActivityTypeId = activityTypeId;
    }

    public int getColorIndicator() {
        return ColorIndicator;
    }

    public void setColorIndicator(int colorIndicator) {
        ColorIndicator = colorIndicator;
    }

    public String getIsMultipleEntries() {
        return IsMultipleEntries;
    }

    public void setIsMultipleEntries(String isMultipleEntries) {
        IsMultipleEntries = isMultipleEntries;
    }

    public String getActicityType() {
        return ActicityType;
    }

    public void setActicityType(String acticityType) {
        ActicityType = acticityType;
    }

    public String getActivityCode() {
        return ActivityCode;
    }

    public void setActivityCode(String activityCode) {
        ActivityCode = activityCode;
    }

    public String getActivityName() {
        return ActivityName;
    }

    public void setActivityName(String activityName) {
        ActivityName = activityName;
    }

    public int getStatusTypeId() {
        return StatusTypeId;
    }

    public void setStatusTypeId(int statusTypeId) {
        StatusTypeId = statusTypeId;
    }

    public String getActivityStatus() {
        return ActivityStatus;
    }

    public void setActivityStatus(String activityStatus) {
        ActivityStatus = activityStatus;
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

    public String getTargetDate() {
        return TargetDate;
    }

    public void setTargetDate(String targetDate) {
        TargetDate = targetDate;
    }

    public String getBuffer1Date() {
        return Buffer1Date;
    }

    public void setBuffer1Date(String buffer1Date) {
        Buffer1Date = buffer1Date;
    }

    public String getBuffer2Date() {
        return Buffer2Date;
    }

    public void setBuffer2Date(String buffer2Date) {
        Buffer2Date = buffer2Date;
    }
}
