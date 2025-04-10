package com.calibrage.palmroot.dbmodels;

public class RMTransactions_New {
   // private  int Id;
    private  String TransactionId;
    private String NurseryCode;
    private  int ActivityId;
    private String ActivityName;
    private int ActivityTypeId;
    private  int StatusTypeId;
    private String TransactionDate;
    private int MaleRegular;
    private int FemaleRegular;
    private int MaleOutside;
    private int FemaleOutside;
    private double MaleRegularCost;
    private double FemaleRegularCost;
    private double MaleOutsideCost;
    private double FemaleoutsideCost;
    private  String ExpenseType;
    private int UOMId;
    private double Quantity;
    private  double TotalCost;
    private String Comments;
    private String FileName;
    private String FileLocation;
    private String FileExtension;
    private int CreatedByUserId;
    private String CreatedDate;
    private String UpdatedByUserId;
    private String UpdatedDate;
    private int ServerUpdatedStatus;
    private  String Remarks;

    public String getRemarks() {
        return Remarks;
    }

    public void setRemarks(String remarks) {
        Remarks = remarks;
    }

    private String ByteImage;

    public String getByteImage() {
        return ByteImage;
    }

    public void setByteImage(String byteImage) {
        ByteImage = byteImage;
    }

    public String getUpdatedByUserId() {
        return UpdatedByUserId;
    }

    public void setUpdatedByUserId(String updatedByUserId) {
        UpdatedByUserId = updatedByUserId;
    }

    public String getUpdatedDate() {
        return UpdatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        UpdatedDate = updatedDate;
    }

//    public int getId() {
//        return Id;
//    }
//
//    public void setId(int id) {
//        Id = id;
//    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public String getNurseryCode() {
        return NurseryCode;
    }

    public void setNurseryCode(String nurseryCode) {
        NurseryCode = nurseryCode;
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

    public String getTransactionDate() {
        return TransactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        TransactionDate = transactionDate;
    }

    public int getMaleRegular() {
        return MaleRegular;
    }

    public void setMaleRegular(int maleRegular) {
        MaleRegular = maleRegular;
    }

    public int getFemaleRegular() {
        return FemaleRegular;
    }

    public void setFemaleRegular(int femaleRegular) {
        FemaleRegular = femaleRegular;
    }

    public int getMaleOutside() {
        return MaleOutside;
    }

    public void setMaleOutside(int maleOutside) {
        MaleOutside = maleOutside;
    }

    public int getFemaleOutside() {
        return FemaleOutside;
    }

    public void setFemaleOutside(int femaleOutside) {
        FemaleOutside = femaleOutside;
    }

    public double getMaleRegularCost() {
        return MaleRegularCost;
    }

    public void setMaleRegularCost(double maleRegularCost) {
        MaleRegularCost = maleRegularCost;
    }

    public double getFemaleRegularCost() {
        return FemaleRegularCost;
    }

    public void setFemaleRegularCost(double femaleRegularCost) {
        FemaleRegularCost = femaleRegularCost;
    }

    public double getMaleOutsideCost() {
        return MaleOutsideCost;
    }

    public void setMaleOutsideCost(double maleOutsideCost) {
        MaleOutsideCost = maleOutsideCost;
    }

    public double getFemaleoutsideCost() {
        return FemaleoutsideCost;
    }

    public void setFemaleoutsideCost(double femaleoutsideCost) {
        FemaleoutsideCost = femaleoutsideCost;
    }

    public String getExpenseType() {
        return ExpenseType;
    }

    public void setExpenseType(String expenseType) {
        ExpenseType = expenseType;
    }

    public int getUOMId() {
        return UOMId;
    }

    public void setUOMId(int UOMId) {
        this.UOMId = UOMId;
    }

    public double getQuantity() {
        return Quantity;
    }

    public void setQuantity(double quantity) {
        Quantity = quantity;
    }

    public double getTotalCost() {
        return TotalCost;
    }

    public void setTotalCost(double totalCost) {
        TotalCost = totalCost;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

//    public String getFileName() {
//        return FileName;
//    }
//
//    public void setFileName(String fileName) {
//        FileName = fileName;
//    }
//
//    public String getFileLocation() {
//        return FileLocation;
//    }
//
//    public void setFileLocation(String fileLocation) {
//        FileLocation = fileLocation;
//    }
//
//    public String getFileExtension() {
//        return FileExtension;
//    }
//
//    public void setFileExtension(String fileExtension) {
//        FileExtension = fileExtension;
//    }

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






