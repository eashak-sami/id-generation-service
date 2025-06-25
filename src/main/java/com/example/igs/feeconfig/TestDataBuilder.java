package com.example.igs.feeconfig;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestDataBuilder {

    public static TransactionConfig createTestTransactionConfig() {
        // AIT for each commission
        FeeDetail ait1 = createFeeDetail("F/R", 0.15, 5, 100, "CU", "ST");
        FeeDetail ait2 = createFeeDetail("F/R", 0.25, 5, 100, "CU", "ST");

        // Commission Distributions with different AITs
        CommissionDistribution commission1 = new CommissionDistribution();
        commission1.setFeeDetail(createFeeDetail("F/R", 0.1, 5, 100, "CU", "ST"));
        commission1.setAit(ait1);

        CommissionDistribution commission2 = new CommissionDistribution();
        commission2.setFeeDetail(createFeeDetail("F/R", 0.2, 5, 100, "CU", "ST"));
        commission2.setAit(ait2);

        List<CommissionDistribution> commissions = Arrays.asList(commission1, commission2);

        // VAT config
        FeeDetail vatConfig = createFeeDetail("F/R", 100, 5, 100, "CU", "ST");

        // Fee Bearer Distribution 1
        FeeBearerDistribution bearer1 = new FeeBearerDistribution();
        bearer1.setFeeDetail(createFeeDetail("F/R", 15, 5, 100, "CU", "ST"));
        bearer1.setVatConfig(vatConfig);
        bearer1.setCommissionDistribution(commissions);

        // Fee Bearer Distribution 2
        FeeBearerDistribution bearer2 = new FeeBearerDistribution();
        bearer2.setFeeDetail(createFeeDetail("F/R", 10, 5, 100, "CU", "ST"));
        bearer2.setVatConfig(vatConfig);
        bearer2.setCommissionDistribution(commissions);

        List<FeeBearerDistribution> bearerDistributions = Arrays.asList(bearer1, bearer2);

        // Fee Slab Config
        FeeSlabConfig feeSlabConfig = new FeeSlabConfig();
        feeSlabConfig.setFeeDetail(createFeeDetail("F/R", 100, 5, 100, null, null));
        feeSlabConfig.setFeeBearerDistribution(bearerDistributions);

        // Transaction Config
        TransactionConfig config = new TransactionConfig();
        config.setTransactionType("001");
        config.setFromSvcId("0000000123433");
        config.setToSvcId("023498889328");
        config.setFeeSlabConfig(List.of(feeSlabConfig));

        process(config);

        return config;
    }

    private static FeeDetail createFeeDetail(String type, double value, double lower, double upper, String from, String to) {
        FeeDetail detail = new FeeDetail();
        detail.setFeeRateType(type);
        detail.setFeeValue(value);
        detail.setFeeLowerLimit(lower);
        detail.setFeeUpperLimit(upper);
        detail.setFromRoleCd(from);
        detail.setToRoleCd(to);
        return detail;
    }

    private static void process(TransactionConfig config) {
        Processing processing = new Processing(config.getTransactionType(), config.getFromSvcId(), config.getToSvcId());

        List<ClearingCode> clearingCodes = new ArrayList<>();
        List<ClearingTransaction> clearingTransactions = new ArrayList<>();
        List<FeeRate> feeRates = new ArrayList<>();

        for (FeeSlabConfig slabConfig : config.getFeeSlabConfig()) {
            //configure base service rate clearing code
            ClearingCode clearingCode = new ClearingCode(String.valueOf(new Random().nextInt(1000)), "service rate");
            clearingCodes.add(clearingCode);
            FeeDetail serviceRateFeeRateDetail = slabConfig.getFeeDetail();
            //configure base fee rate
            FeeRate svcRateFeeRate = new FeeRate(serviceRateFeeRateDetail.getFeeRateType(), serviceRateFeeRateDetail.getFeeValue(),
                    serviceRateFeeRateDetail.getFeeLowerLimit(), serviceRateFeeRateDetail.getFeeUpperLimit(),
                    serviceRateFeeRateDetail.getFromRoleCd(), serviceRateFeeRateDetail.getToRoleCd());
            feeRates.add(svcRateFeeRate);

            List<FeeBearerDistribution> feeBearers = slabConfig.getFeeBearerDistribution();

            for (FeeBearerDistribution feeBearer : feeBearers) {
                //calculation for fee clearing...
                ClearingCode feeClearingCode = new ClearingCode(String.valueOf(new Random().nextInt(1000)), "fee clearing code");
                clearingCodes.add(feeClearingCode);

                ClearingTransaction feeClearingTxn = new ClearingTransaction(feeClearingCode.getClearingCode(),
                        clearingCode.getClearingCode());
                clearingTransactions.add(feeClearingTxn);

                FeeDetail feesConfigFeeDetail = feeBearer.getFeeDetail();
                FeeRate feesConfigFeeRate = new FeeRate(feesConfigFeeDetail.getFeeRateType(), feesConfigFeeDetail.getFeeValue(),
                        feesConfigFeeDetail.getFeeLowerLimit(), feesConfigFeeDetail.getFeeUpperLimit(),
                        feesConfigFeeDetail.getFromRoleCd(), feesConfigFeeDetail.getToRoleCd());
                feeRates.add(feesConfigFeeRate);

                //calculation for vat clearing...
                ClearingCode vatClearingCode = new ClearingCode(String.valueOf(new Random().nextInt(1000)), "vat clearing code");
                clearingCodes.add(vatClearingCode);

                ClearingTransaction vatClearingTxn = new ClearingTransaction(vatClearingCode.getClearingCode(),
                        feeClearingCode.getClearingCode());
                clearingTransactions.add(vatClearingTxn);

                FeeDetail vatConfigFeeDetail = feeBearer.getFeeDetail();
                FeeRate vatConfigFeeRate = new FeeRate(vatConfigFeeDetail.getFeeRateType(), vatConfigFeeDetail.getFeeValue(),
                        vatConfigFeeDetail.getFeeLowerLimit(), vatConfigFeeDetail.getFeeUpperLimit(),
                        vatConfigFeeDetail.getFromRoleCd(), vatConfigFeeDetail.getToRoleCd());
                feeRates.add(vatConfigFeeRate);


                //calculation for commission clearing
                for (CommissionDistribution commissionDistribution : feeBearer.getCommissionDistribution()) {
                    //calculation for commission clearing...
                    ClearingCode commissionClearingCode = new ClearingCode(String.valueOf(new Random().nextInt(1000)), "commission clearing code");
                    clearingCodes.add(commissionClearingCode);

                    ClearingTransaction commissionClearingTxn = new ClearingTransaction(commissionClearingCode.getClearingCode(),
                            clearingCode.getClearingCode());
                    clearingTransactions.add(commissionClearingTxn);

                    FeeDetail commissionConfigFeeDetail = commissionDistribution.getFeeDetail();
                    FeeRate commissionConfigFeeRate = new FeeRate(commissionConfigFeeDetail.getFeeRateType(), commissionConfigFeeDetail.getFeeValue(),
                            commissionConfigFeeDetail.getFeeLowerLimit(), commissionConfigFeeDetail.getFeeUpperLimit(),
                            commissionConfigFeeDetail.getFromRoleCd(), commissionConfigFeeDetail.getToRoleCd());
                    feeRates.add(commissionConfigFeeRate);

                    //calculation for ait clearing...
                    ClearingCode aitClearingCode = new ClearingCode(String.valueOf(new Random().nextInt(1000)), "ait clearing code");
                    clearingCodes.add(aitClearingCode);

                    ClearingTransaction aitClearingTxn = new ClearingTransaction(aitClearingCode.getClearingCode(),
                            commissionClearingCode.getClearingCode());
                    clearingTransactions.add(aitClearingTxn);

                    FeeDetail aitConfigFeeDetail = commissionDistribution.getAit();
                    FeeRate aitConfigFeeRate = new FeeRate(aitConfigFeeDetail.getFeeRateType(), aitConfigFeeDetail.getFeeValue(),
                            aitConfigFeeDetail.getFeeLowerLimit(), aitConfigFeeDetail.getFeeUpperLimit(),
                            aitConfigFeeDetail.getFromRoleCd(), aitConfigFeeDetail.getToRoleCd());
                    feeRates.add(aitConfigFeeRate);
                }
            }
        }

        //printing values

        for (ClearingCode clearingCode : clearingCodes) {
            System.out.println(clearingCode.getClearingCode() + " " + clearingCode.getName());
        }

        for (ClearingTransaction clearingTransaction : clearingTransactions) {
            System.out.println(clearingTransaction.getClearingCode() + " " + clearingTransaction.getServiceRateClearingCode());
        }
    }
}
