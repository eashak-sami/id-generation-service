package com.example.igs.feeconfig;

public class ClearingCode {
    private String clearingCode;
    private String name;

    public String getClearingCode() {
        return clearingCode;
    }

    public void setClearingCode(String clearingCode) {
        this.clearingCode = clearingCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ClearingCode(String clearingCode, String name) {
        this.clearingCode = clearingCode;
        this.name = name;
    }
}
