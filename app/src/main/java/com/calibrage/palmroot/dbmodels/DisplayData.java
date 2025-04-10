package com.calibrage.palmroot.dbmodels;

public class DisplayData {

    private int  FieldId;
    private String InputType;
    private String  Value;
    private int ServerUpdatedStatus;
    private String TransactionId;

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public int getServerUpdatedStatus() {
        return ServerUpdatedStatus;
    }

    public void setServerUpdatedStatus(int serverUpdatedStatus) {
        ServerUpdatedStatus = serverUpdatedStatus;
    }

    public int getFieldId() {
        return FieldId;
    }

    public void setFieldId(int fieldId) {
        FieldId = fieldId;
    }

    public String getInputType() {
        return InputType;
    }

    public void setInputType(String inputType) {
        InputType = inputType;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }
}
