package org.mpay.utilityservice.batch.processor;

import lombok.extern.slf4j.Slf4j;
import org.mpay.utilityservice.dto.BillValidationResult;
import org.mpay.utilityservice.dto.ElectricityBillRaw;
import org.mpay.utilityservice.entity.ElectricityBill;
import org.mpay.utilityservice.util.BillMapper;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class ElectricityBillProcessor implements ItemProcessor<ElectricityBillRaw, BillValidationResult> {

    @Override
    public BillValidationResult process(ElectricityBillRaw item) {

        if (item == null) {
            return null;
        }

        List<String> errors = new ArrayList<>();

        validateRequired(item.ledgerNo(), "Ledger No", errors);
        validateRequired(item.consumerNo(), "Consumer No", errors);
        validateRequired(item.consumerName(), "Consumer Name", errors);

        ElectricityBill entity = BillMapper.mapToEntity(item, errors);

        if (!errors.isEmpty()) {
            log.warn("Validation error at Row {}: {}", item.rowNumber(), String.join(", ", errors));
            return new BillValidationResult(item, null, false, String.join(" | ", errors));
        }

        return new BillValidationResult(item, entity, true, null);
    }


    private void validateRequired(String val, String field, List<String> errors) {
        if (val == null || val.isBlank() || val.equalsIgnoreCase("null")) {
            errors.add(field + " is mandatory");
        }
    }
}