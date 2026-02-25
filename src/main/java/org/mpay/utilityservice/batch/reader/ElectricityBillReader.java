package org.mpay.utilityservice.batch.reader;

import lombok.extern.slf4j.Slf4j;
import org.mpay.utilityservice.dto.ElectricityBillRaw;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.poi.PoiItemReader;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ElectricityBillReader {

    @Bean
    @StepScope
    public PoiItemReader<ElectricityBillRaw> excelBillReader(
            @Value("#{jobParameters['localFilePath']}") String path) {

        log.info("Initializing Excel Reader for file: {}", path);
        PoiItemReader<ElectricityBillRaw> reader = new PoiItemReader<>();
        reader.setName("electricityBillExcelReader");
        reader.setResource(new FileSystemResource(path));

        // LinesToSkip(1) is cleaner for skipping headers in PoiItemReader
        reader.setLinesToSkip(1);
        reader.setRowMapper(billRowMapper());

        // Essential for multi-threaded steps to prevent state-save conflicts
        reader.setSaveState(false);
        return reader;
    }

    private RowMapper<ElectricityBillRaw> billRowMapper() {
        return (RowSet rowSet) -> {
            String[] row = rowSet.getCurrentRow();

            // 1. Check for physical null row or empty array
            if (row == null || row.length == 0) {
                return null;
            }

            // 2. Strict Ghost Row Detection:
            // If the first two mandatory columns are empty, we hit the end of data.
            String ledgerNo = safeGet(row, 0);
            String consumerNo = safeGet(row, 1);

            if (ledgerNo.isEmpty() && consumerNo.isEmpty()) {
                log.debug("End of data reached at row index: {}", rowSet.getCurrentRowIndex());
                return null;
            }

            // Excel row index is 0-based, adding 1 for user-friendly logging (e.g., Row 2)
            int currentRowIndex = rowSet.getCurrentRowIndex() + 1;

            return new ElectricityBillRaw(
                    currentRowIndex,
                    ledgerNo,
                    consumerNo,
                    safeGet(row, 2), // meterNo
                    safeGet(row, 3), // consumerName
                    safeGet(row, 4), // address
                    safeGet(row, 5), // billCode
                    safeGet(row, 6), // billDueDate
                    safeGet(row, 7), // usedUnit
                    safeGet(row, 8), // billAmount
                    safeGet(row, 9), // serviceCharges
                    safeGet(row, 10), // horsePowerCharges
                    safeGet(row, 11), // discount
                    safeGet(row, 12), // lastBalance
                    safeGet(row, 13), // totalBillAmount
                    safeGet(row, 14), // debtBalance
                    safeGet(row, 15), // installationFee
                    safeGet(row, 16), // grandTotalAmount
                    safeGet(row, 17), // township
                    safeGet(row, 18), // terrifCode
                    safeGet(row, 19), // readingDate
                    safeGet(row, 20), // previousUnit
                    safeGet(row, 21), // currentUnit
                    safeGet(row, 22), // houseNo
                    safeGet(row, 23), // road
                    safeGet(row, 24), // quarter
                    safeGet(row, 25), // area
                    safeGet(row, 26), // billNo
                    safeGet(row, 27)  // deposit
            );
        };
    }

    private String safeGet(String[] row, int index) {
        if (row == null || index >= row.length || row[index] == null) {
            return "";
        }
        return row[index].trim();
    }
}