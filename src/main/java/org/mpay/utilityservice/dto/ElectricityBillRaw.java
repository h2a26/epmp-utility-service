package org.mpay.utilityservice.dto;

public record ElectricityBillRaw(
        int rowNumber,
        String ledgerNo,
        String consumerNo,
        String meterNo,
        String consumerName,
        String address,
        String billCode,
        String billDueDate,
        String usedUnit,
        String billAmount,
        String serviceCharges,
        String horsePowerCharges,
        String discount,
        String lastBalance,
        String totalBillAmount,
        String debtBalance,
        String installationFee,
        String grandTotalAmount,
        String township,
        String terrifCode,
        String readingDate,
        String previousUnit,
        String currentUnit,
        String houseNo,
        String road,
        String quarter,
        String area,
        String billNo,
        String deposit
) {
}

