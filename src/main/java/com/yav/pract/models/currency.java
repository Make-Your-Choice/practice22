package com.yav.pract.models;

import java.util.Date;

public class currency {
    private String cbId;
    private String name;
    private String numCode;
    private String charCode;
    private Date dateRec;
    private double value;
    public currency() {
    }
    public currency(String cbId, String name, String numCode,
                    Date dateRec, String charCode, double value) {
        this.cbId = cbId;
        this.name = name;
        this.numCode = numCode;
        this.charCode = charCode;
        this.dateRec = dateRec;
        this.value = value;
    }
    public String getCbId() {
        return cbId;
    }
    public void setCbId(String cbId) {
        this.cbId = cbId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getNumCode() {
        return numCode;
    }
    public void setNumCode(String numCode) {
        this.numCode = numCode;
    }
    public String getCharCode() {
        return charCode;
    }
    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }
    public Date getDateRec() { return dateRec; }
    public void setDateRec(Date dateRec) { this.dateRec = dateRec; }
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
}
