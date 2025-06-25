package com.example.igs.feeconfig;

import java.util.List;

public class FeeSlabConfig {
    private FeeDetail feeDetail;
    private List<FeeBearerDistribution> feeBearerDistribution;

    // Getters and Setters


    public FeeDetail getFeeDetail() {
        return feeDetail;
    }

    public void setFeeDetail(FeeDetail feeDetail) {
        this.feeDetail = feeDetail;
    }

    public List<FeeBearerDistribution> getFeeBearerDistribution() {
        return feeBearerDistribution;
    }

    public void setFeeBearerDistribution(List<FeeBearerDistribution> feeBearerDistribution) {
        this.feeBearerDistribution = feeBearerDistribution;
    }
}