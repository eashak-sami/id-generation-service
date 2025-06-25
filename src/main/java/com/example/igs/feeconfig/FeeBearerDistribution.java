package com.example.igs.feeconfig;

import java.util.List;

public class FeeBearerDistribution {
    private FeeDetail feeDetail;
    private FeeDetail vatConfig;
    private List<CommissionDistribution> commissionDistribution;

    // Getters and Setters


    public FeeDetail getFeeDetail() {
        return feeDetail;
    }

    public void setFeeDetail(FeeDetail feeDetail) {
        this.feeDetail = feeDetail;
    }

    public List<CommissionDistribution> getCommissionDistribution() {
        return commissionDistribution;
    }

    public void setCommissionDistribution(List<CommissionDistribution> commissionDistribution) {
        this.commissionDistribution = commissionDistribution;
    }

    public FeeDetail getVatConfig() {
        return vatConfig;
    }

    public void setVatConfig(FeeDetail vatConfig) {
        this.vatConfig = vatConfig;
    }
}