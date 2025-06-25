package com.example.igs.feeconfig;

public class Processing {
    private String transactionType;
    private String fromSvcId;
    private String toSvcId;

    public Processing(String transactionType, String fromSvcId, String toSvcId) {
        this.transactionType = transactionType;
        this.fromSvcId = fromSvcId;
        this.toSvcId = toSvcId;
    }

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
}
