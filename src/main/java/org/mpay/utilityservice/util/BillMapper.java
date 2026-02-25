package org.mpay.utilityservice.util;

import org.mpay.utilityservice.dto.BillValidationResult;
import org.mpay.utilityservice.dto.ElectricityBillRaw;
import org.mpay.utilityservice.entity.ElectricityBill;
import org.mpay.utilityservice.entity.FailedElectricityBill;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BillMapper {
    // Strict format requirement: 2026-02-17
    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static LocalDate parseDueDate(String val, List<String> errors) {
        try {
            if (val == null || val.isBlank()) {
                errors.add("Bill Due Date is missing");
                return null;
            }
            return LocalDate.parse(val.trim(), ISO_DATE);
        } catch (Exception e) {
            errors.add("Invalid date format [" + val + "]. Expected YYYY-MM-DD");
            return null;
        }
    }

    public static BigDecimal parseNumeric(String val, String fieldName, List<String> errors) {
        try {
            if (val == null || val.isBlank() || val.equalsIgnoreCase("null")) return BigDecimal.ZERO;
            // Handle commas or spaces often found in manual Excel entries
            String cleanVal = val.trim().replace(",", "").replace(" ", "");
            return new BigDecimal(cleanVal);
        } catch (Exception e) {
            errors.add(fieldName + " must be numeric [" + val + "]");
            return null;
        }
    }

    public static Integer parseInteger(String val, String fieldName, List<String> errors) {
        try {
            if (val == null || val.isBlank() || val.equalsIgnoreCase("null")) return 0;
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            errors.add(fieldName + " must be an integer [" + val + "]");
            return null;
        }
    }

    public static ElectricityBill mapToEntity(ElectricityBillRaw raw, List<String> errors) {
        // Attempt to parse dates first as they determine billMonth/billYear
        LocalDate dueDate = parseDueDate(raw.billDueDate(), errors);

        return ElectricityBill.builder()
                .ledgerNo(raw.ledgerNo())
                .consumerNo(raw.consumerNo())
                .meterNo(raw.meterNo())
                .consumerName(raw.consumerName())
                .address(raw.address())
                .billCode(raw.billCode())
                .billDueDate(dueDate)
                .billMonth(dueDate != null ? dueDate.getMonthValue() : null)
                .billYear(dueDate != null ? dueDate.getYear() : null)
                .usedUnit(parseInteger(raw.usedUnit(), "Used Unit", errors))
                .billAmount(parseNumeric(raw.billAmount(), "Bill Amount", errors))
                .serviceCharges(parseNumeric(raw.serviceCharges(), "Service Charges", errors))
                .horsePowerCharges(parseNumeric(raw.horsePowerCharges(), "HP Charges", errors))
                .discount(parseNumeric(raw.discount(), "Discount", errors))
                .lastBalance(parseNumeric(raw.lastBalance(), "Last Balance", errors))
                .totalBillAmount(parseNumeric(raw.totalBillAmount(), "Total Bill Amount", errors))
                .debtBalance(parseNumeric(raw.debtBalance(), "Debt Balance", errors))
                .installationFee(parseNumeric(raw.installationFee(), "Installation Fee", errors))
                .grandTotalAmount(parseNumeric(raw.grandTotalAmount(), "Grand Total", errors))
                .isActiveConsumer(true)
                .township(raw.township())
                .terrifCode(raw.terrifCode())
                .previousUnit(parseInteger(raw.previousUnit(), "Prev Unit", errors))
                .currentUnit(parseInteger(raw.currentUnit(), "Curr Unit", errors))
                .houseNo(raw.houseNo())
                .road(raw.road())
                .quarter(raw.quarter())
                .area(raw.area())
                .billNo(raw.billNo())
                .deposit(parseNumeric(raw.deposit(), "Deposit", errors))
                .build();
    }

    public static FailedElectricityBill mapToFailedEntity(BillValidationResult result, Long jobId) {
        ElectricityBillRaw raw = result.rawData();
        return FailedElectricityBill.builder()
                .jobId(jobId)
                .failureReason(result.errorMsg())
                .ledgerNo(raw.ledgerNo())
                .consumerNo(raw.consumerNo())
                .meterNo(raw.meterNo())
                .consumerName(raw.consumerName())
                .address(raw.address())
                .billCode(raw.billCode())
                .billDueDate(raw.billDueDate())
                .usedUnit(raw.usedUnit())
                .billAmount(raw.billAmount())
                .serviceCharges(raw.serviceCharges())
                .horsePowerCharges(raw.horsePowerCharges())
                .discount(raw.discount())
                .lastBalance(raw.lastBalance())
                .totalBillAmount(raw.totalBillAmount())
                .debtBalance(raw.debtBalance())
                .installationFee(raw.installationFee())
                .grandTotalAmount(raw.grandTotalAmount())
                .township(raw.township())
                .terrifCode(raw.terrifCode())
                .readingDate(raw.readingDate())
                .previousUnit(raw.previousUnit())
                .currentUnit(raw.currentUnit())
                .houseNo(raw.houseNo())
                .road(raw.road())
                .quarter(raw.quarter())
                .area(raw.area())
                .billNo(raw.billNo())
                .deposit(raw.deposit())
                .build();
    }
}