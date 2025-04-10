package com.calibrage.palmroot.dbmodels;

public class NurseryLabourLog {
    int Id;
    String LogDate;
    double RegularMale;
    double RegularFemale;
    double ContractMale;
    double ContractFemale;
    int IsActive;
    int CreatedByUserId;
    String CreatedDate;
    int UpdatedByUserId;
    String UpdatedDate;
    int ServerUpdatedStatus;
    String NurseryCode;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getLogDate() {
        return LogDate;
    }

    public void setLogDate(String logDate) {
        LogDate = logDate;
    }

    public double getRegularMale() {
        return RegularMale;
    }

    public void setRegularMale(double regularMale) {
        RegularMale = regularMale;
    }

    public double getRegularFemale() {
        return RegularFemale;
    }

    public void setRegularFemale(double regularFemale) {
        RegularFemale = regularFemale;
    }

    public double getContractMale() {
        return ContractMale;
    }

    public void setContractMale(double contractMale) {
        ContractMale = contractMale;
    }

    public double getContractFemale() {
        return ContractFemale;
    }

    public void setContractFemale(double contractFemale) {
        ContractFemale = contractFemale;
    }

    public int getIsActive() {
        return IsActive;
    }

    public void setIsActive(int isActive) {
        IsActive = isActive;
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

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    public String getNurseryCode() {
        return NurseryCode;
    }

    public void setNurseryCode(String nurseryCode) {
        NurseryCode = nurseryCode;
    }
}
