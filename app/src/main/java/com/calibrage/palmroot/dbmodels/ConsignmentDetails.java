package com.calibrage.palmroot.dbmodels;

public class ConsignmentDetails {

    private  int Id;
    private  String NurseryCode;
    private String ConsignmentCode;
    private  int OriginId;
    private  int  VendorId;
    private  int VarietyId;
    private  String PurchaseDate;
    private  String EstimatedDate;
    private  int EstimatedQuantity;
    private  int StatusTypeId;
    private  String ArrivalDate;
    private  int ReceivedQuantity;
    private  String SowingDate;
    private  int IsActive;
    private  int CreatedByUserId;
    private String CreatedDate;
    private int UpdatedByUserId;
    private String UpdatedDate;
    private  int ServerUpdatedStatus;


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

    public String getConsignmentCode() {
        return ConsignmentCode;
    }

    public void setConsignmentCode(String consignmentCode) {
        ConsignmentCode = consignmentCode;
    }

    public int getOriginId() {
        return OriginId;
    }

    public void setOriginId(int originId) {
        OriginId = originId;
    }

    public int getVendorId() {
        return VendorId;
    }

    public void setVendorId(int vendorId) {
        VendorId = vendorId;
    }

    public int getVarietyId() {
        return VarietyId;
    }

    public void setVarietyId(int varietyId) {
        VarietyId = varietyId;
    }

    public String getPurchaseDate() {
        return PurchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        PurchaseDate = purchaseDate;
    }

    public String getEstimatedDate() {
        return EstimatedDate;
    }

    public void setEstimatedDate(String estimatedDate) {
        EstimatedDate = estimatedDate;
    }

    public int getEstimatedQuantity() {
        return EstimatedQuantity;
    }

    public void setEstimatedQuantity(int estimatedQuantity) {
        EstimatedQuantity = estimatedQuantity;
    }

    public int getStatusTypeId() {
        return StatusTypeId;
    }

    public void setStatusTypeId(int statusTypeId) {
        StatusTypeId = statusTypeId;
    }

    public String getArrivalDate() {
        return ArrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        ArrivalDate = arrivalDate;
    }

    public int getReceivedQuantity() {
        return ReceivedQuantity;
    }

    public void setReceivedQuantity(int receivedQuantity) {
        ReceivedQuantity = receivedQuantity;
    }

    public String getSowingDate() {
        return SowingDate;
    }

    public void setSowingDate(String sowingDate) {
        SowingDate = sowingDate;
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
}
