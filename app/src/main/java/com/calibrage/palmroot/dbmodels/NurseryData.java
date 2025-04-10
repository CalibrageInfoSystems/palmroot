package com.calibrage.palmroot.dbmodels;

public class NurseryData {

    private  String Code;
    private String Name;
    private  int PinCode;
    private  String Statename;
    private String Districtname;
    private String villagename;
    private String mandalname;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getPinCode() {
        return PinCode;
    }

    public void setPinCode(int pinCode) {
        PinCode = pinCode;
    }

    public String getStatename() {
        return Statename;
    }

    public void setStatename(String statename) {
        Statename = statename;
    }

    public String getDistrictname() {
        return Districtname;
    }

    public void setDistrictname(String districtname) {
        Districtname = districtname;
    }

    public String getVillagename() {
        return villagename;
    }

    public void setVillagename(String villagename) {
        this.villagename = villagename;
    }

    public String getMandalname() {
        return mandalname;
    }

    public void setMandalname(String mandalname) {
        this.mandalname = mandalname;
    }
}
