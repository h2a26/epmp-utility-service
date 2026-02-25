package org.mpay.utilityservice.dto;

import org.mpay.utilityservice.entity.ElectricityBill;

public record BillValidationResult(
        ElectricityBillRaw rawData,
        ElectricityBill validatedEntity,
        boolean isValid,
        String errorMsg
) {}