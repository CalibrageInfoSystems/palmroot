package com.calibrage.palmroot.dbmodels;

public class Saplings {

    private Integer Id;
    private String NurseryCode;
    private String ConsignmentCode;
    private Integer OriginId;
    private Integer VendorId;
    private Integer VarietyId;

    private String PurchaseDate;
    private String EstimatedDate;
    private Integer EstimatedQuantity;

    private int IsActive;
    private int CreatedByUserId;
    private String CreatedDate;
    private int UpdatedByUserId;
    private String UpdatedDate;
    private int ServerUpdatedStatus;
    private int StatusTypeId;
    private  String ArrivedDate;
    private  int ArrivedQuantity;
    private  String SowingDate;
    private  String TransplantingDate;
    private String SAPCode;
    private  int CurrentClosingStock;

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

    public String getSowingDate() {
        return SowingDate;
    }

    public void setSowingDate(String sowingDate) {
        SowingDate = sowingDate;
    }

    public String getTransplantingDate() {
        return TransplantingDate;
    }

    public void setTransplantingDate(String transplantingDate) {
        TransplantingDate = transplantingDate;
    }

    public String getSAPCode() {
        return SAPCode;
    }

    public void setSAPCode(String SAPCode) {
        this.SAPCode = SAPCode;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
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

    public Integer getOriginId() {
        return OriginId;
    }

    public void setOriginId(Integer originId) {
        OriginId = originId;
    }

    public Integer getVendorId() {
        return VendorId;
    }

    public void setVendorId(Integer vendorId) {
        VendorId = vendorId;
    }

    public Integer getVarietyId() {
        return VarietyId;
    }

    public void setVarietyId(Integer varietyId) {
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

    public Integer getEstimatedQuantity() {
        return EstimatedQuantity;
    }

    public void setEstimatedQuantity(Integer estimatedQuantity) {
        EstimatedQuantity = estimatedQuantity;
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

    public int getStatusTypeId() {
        return StatusTypeId;
    }

    public void setStatusTypeId(int statusTypeId) {
        StatusTypeId = statusTypeId;
    }

    public int getCurrentClosingStock() {
        return CurrentClosingStock;
    }

    public void setCurrentClosingStock(int currentClosingStock) {
        CurrentClosingStock = currentClosingStock;
    }
}
