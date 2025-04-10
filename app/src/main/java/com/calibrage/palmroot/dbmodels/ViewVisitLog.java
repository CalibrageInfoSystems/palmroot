package com.calibrage.palmroot.dbmodels;

public class ViewVisitLog {
    private  String NurseryCode;
    private  String Nurseryname;
    private  int LogTypeId;

    public String getNurseryCode() {
        return NurseryCode;
    }

    public void setNurseryCode(String nurseryCode) {
        NurseryCode = nurseryCode;
    }

    public String getNurseryname() {
        return Nurseryname;
    }

    public void setNurseryname(String nurseryname) {
        Nurseryname = nurseryname;
    }

    public int getLogTypeId() {
        return LogTypeId;
    }

    public void setLogTypeId(int logTypeId) {
        LogTypeId = logTypeId;
    }

    public String getLogtype() {
        return logtype;
    }

    public void setLogtype(String logtype) {
        this.logtype = logtype;
    }

    public String getCosignmentCode() {
        return CosignmentCode;
    }

    public void setCosignmentCode(String cosignmentCode) {
        CosignmentCode = cosignmentCode;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    public String getLogDate() {
        return LogDate;
    }

    public void setLogDate(String logDate) {
        LogDate = logDate;
    }

    public String getComments() {
        return Comments;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getFileLocation() {
        return FileLocation;
    }

    public void setFileLocation(String fileLocation) {
        FileLocation = fileLocation;
    }

    private  String logtype;
    private String CosignmentCode;
    private String ClientName;
    private String LogDate;
    private String Comments;
    private String FileLocation;
}
