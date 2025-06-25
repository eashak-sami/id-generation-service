package com.example.igs.feeconfig;

public class ClearingTransaction {
    private String clearingCode;
    private String serviceRateClearingCode;

    public String getClearingCode() {
        return clearingCode;
    }

    public void setClearingCode(String clearingCode) {
        this.clearingCode = clearingCode;
    }

    public String getServiceRateClearingCode() {
        return serviceRateClearingCode;
    }

    public void setServiceRateClearingCode(String serviceRateClearingCode) {
        this.serviceRateClearingCode = serviceRateClearingCode;
    }

    public ClearingTransaction(String clearingCode, String serviceRateClearingCode) {
        this.clearingCode = clearingCode;
        this.serviceRateClearingCode = serviceRateClearingCode;
    }
}
