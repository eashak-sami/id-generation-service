package com.example.igs.feeconfig;

import java.util.List;

public class TransactionConfig {
    private String transactionType;
    private String fromSvcId;
    private String toSvcId;
    private List<FeeSlabConfig> feeSlabConfig;

    // Getters and Setters


    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getFromSvcId() {
        return fromSvcId;
    }

    public void setFromSvcId(String fromSvcId) {
        this.fromSvcId = fromSvcId;
    }

    public String getToSvcId() {
        return toSvcId;
    }

    public void setToSvcId(String toSvcId) {
        this.toSvcId = toSvcId;
    }

    public List<FeeSlabConfig> getFeeSlabConfig() {
        return feeSlabConfig;
    }

    public void setFeeSlabConfig(List<FeeSlabConfig> feeSlabConfig) {
        this.feeSlabConfig = feeSlabConfig;
    }
}