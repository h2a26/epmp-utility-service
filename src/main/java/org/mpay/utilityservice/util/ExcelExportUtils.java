package org.mpay.utilityservice.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mpay.utilityservice.entity.FailedElectricityBill;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelExportUtils {

    public static byte[] generateFailedBillReport(List<FailedElectricityBill> failedBills) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Failed Bills");

            // Define Headers exactly as per ElectricityBillRaw order + Error Reason
            String[] headers = {
                    "error_reasons",
                    "ledger_no", "consumer_no", "meter_no", "consumer_name", "address",
                    "bill_code", "bill_due_date", "used_unit", "bill_amount", "service_charges",
                    "horse_power_charges", "discount", "last_balance", "total_bill_amount",
                    "debt_balance", "installation_fee", "grand_total_amount", "township",
                    "terrif_code", "reading_date", "previous_unit", "current_unit",
                    "house_no", "road", "quarter", "area", "bill_no", "deposit"
            };
            // Style for Header
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data Rows
            int rowNum = 1;
            for (FailedElectricityBill bill : failedBills) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(bill.getFailureReason());
                row.createCell(1).setCellValue(bill.getLedgerNo());
                row.createCell(2).setCellValue(bill.getConsumerNo());
                row.createCell(3).setCellValue(bill.getMeterNo());
                row.createCell(4).setCellValue(bill.getConsumerName());
                row.createCell(5).setCellValue(bill.getAddress());
                row.createCell(6).setCellValue(bill.getBillCode());
                row.createCell(7).setCellValue(bill.getBillDueDate());
                row.createCell(8).setCellValue(bill.getUsedUnit());
                row.createCell(9).setCellValue(bill.getBillAmount());
                row.createCell(10).setCellValue(bill.getServiceCharges());
                row.createCell(11).setCellValue(bill.getHorsePowerCharges());
                row.createCell(12).setCellValue(bill.getDiscount());
                row.createCell(13).setCellValue(bill.getLastBalance());
                row.createCell(14).setCellValue(bill.getTotalBillAmount());
                row.createCell(15).setCellValue(bill.getDebtBalance());
                row.createCell(16).setCellValue(bill.getInstallationFee());
                row.createCell(17).setCellValue(bill.getGrandTotalAmount());
                row.createCell(18).setCellValue(bill.getTownship());
                row.createCell(19).setCellValue(bill.getTerrifCode());
                row.createCell(20).setCellValue(bill.getReadingDate());
                row.createCell(21).setCellValue(bill.getPreviousUnit());
                row.createCell(22).setCellValue(bill.getCurrentUnit());
                row.createCell(23).setCellValue(bill.getHouseNo());
                row.createCell(24).setCellValue(bill.getRoad());
                row.createCell(25).setCellValue(bill.getQuarter());
                row.createCell(26).setCellValue(bill.getArea());
                row.createCell(27).setCellValue(bill.getBillNo());
                row.createCell(28).setCellValue(bill.getDeposit());
            }

            // Auto-size columns (optional, can be heavy for very large files)
            for (int i = 0; i < 5; i++) { // Auto-size at least the first few important columns
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    public static byte[] generateTemplate() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Template");

            // Headers match the order in ElectricityBillReader.java
            String[] headers = {
                    "ledger_no", "consumer_no", "meter_no", "consumer_name", "address",
                    "bill_code", "bill_due_date", "used_unit", "bill_amount", "service_charges",
                    "horse_power_charges", "discount", "last_balance", "total_bill_amount",
                    "debt_balance", "installation_fee", "grand_total_amount", "township",
                    "terrif_code", "reading_date", "previous_unit", "current_unit",
                    "house_no", "road", "quarter", "area", "bill_no", "deposit"
            };

            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}