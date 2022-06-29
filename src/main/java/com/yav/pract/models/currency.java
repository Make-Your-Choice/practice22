package com.yav.pract.models;

import java.util.Date;

public class currency {
    private String cbId;
    private String name;
    private String numCode;
    private String charCode;
    private double value;
    public currency() {
    }
    public currency(String cbId, String name, String numCode,
                    String charCode, double value) {
        this.cbId = cbId;
        this.name = name;
        this.numCode = numCode;
        this.charCode = charCode;
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
    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }
}
