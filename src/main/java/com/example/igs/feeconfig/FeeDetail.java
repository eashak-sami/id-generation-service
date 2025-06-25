package com.example.igs.feeconfig;

public class FeeDetail {
    private String feeRateType;
    private double feeValue;
    private double feeLowerLimit;
    private double feeUpperLimit;
    private String fromRoleCd;
    private String toRoleCd;

    // Getters and Setters


    public String getFeeRateType() {
        return feeRateType;
    }

    public void setFeeRateType(String feeRateType) {
        this.feeRateType = feeRateType;
    }

    public double getFeeValue() {
        return feeValue;
    }

    public void setFeeValue(double feeValue) {
        this.feeValue = feeValue;
    }

    public double getFeeLowerLimit() {
        return feeLowerLimit;
    }

    public void setFeeLowerLimit(double feeLowerLimit) {
        this.feeLowerLimit = feeLowerLimit;
    }

    public double getFeeUpperLimit() {
        return feeUpperLimit;
    }

    public void setFeeUpperLimit(double feeUpperLimit) {
        this.feeUpperLimit = feeUpperLimit;
    }

    public String getFromRoleCd() {
        return fromRoleCd;
    }

    public void setFromRoleCd(String fromRoleCd) {
        this.fromRoleCd = fromRoleCd;
    }

    public String getToRoleCd() {
        return toRoleCd;
    }

    public void setToRoleCd(String toRoleCd) {
        this.toRoleCd = toRoleCd;
    }
}