package org.mpay.utilityservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "electricity_bill", schema = "prepost")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ElectricityBill {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bill_seq")
    @SequenceGenerator(name = "bill_seq", sequenceName = "prepost.electricity_bill_seq", schema = "prepost", allocationSize = 1)
    private Long id;

    @Column(name = "ledger_no", length = 20, nullable = false)
    private String ledgerNo;

    @Column(name = "consumer_no", length = 30, nullable = false)
    private String consumerNo;

    @Column(name = "meter_no", length = 50, nullable = false)
    private String meterNo;

    @Column(name = "consumer_name", length = 255, nullable = false)
    private String consumerName;

    @Column(name = "address", length = 512)
    private String address;

    @Column(name = "bill_code", length = 16)
    private String billCode;

    @Column(name = "bill_due_date", nullable = false)
    private LocalDate billDueDate;

    @Column(name = "used_unit", nullable = false)
    private Integer usedUnit;

    @Column(name = "bill_amount", precision = 15, scale = 4)
    private BigDecimal billAmount;

    @Column(name = "service_charges", precision = 11, scale = 4)
    private BigDecimal serviceCharges;

    @Column(name = "horse_power_charges", precision = 14, scale = 4)
    private BigDecimal horsePowerCharges;

    @Column(name = "discount", precision = 14, scale = 4)
    private BigDecimal discount;

    @Column(name = "last_balance", precision = 15, scale = 4)
    private BigDecimal lastBalance;

    @Column(name = "total_bill_amount", precision = 15, scale = 4, nullable = false)
    private BigDecimal totalBillAmount;

    @Column(name = "debt_balance", precision = 15, scale = 4)
    private BigDecimal debtBalance;

    @Column(name = "installation_fee", precision = 8, scale = 2)
    private BigDecimal installationFee;

    @Column(name = "grand_total_amount", precision = 15, scale = 4)
    private BigDecimal grandTotalAmount;

    @Column(name = "is_active_consumer", nullable = false)
    private Boolean isActiveConsumer;

    @Column(name = "township", length = 100)
    private String township;

    @Column(name = "bill_month", precision = 2, nullable = false)
    private Integer billMonth;

    @Column(name = "bill_year", precision = 4, nullable = false)
    private Integer billYear;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "terrif_code", length = 6)
    private String terrifCode;

    @Column(name = "reading_date")
    private LocalDateTime readingDate;

    @Column(name = "previous_unit")
    private Integer previousUnit;

    @Column(name = "current_unit")
    private Integer currentUnit;

    @Column(name = "house_no", length = 255)
    private String houseNo;

    @Column(name = "road", length = 255)
    private String road;

    @Column(name = "quarter", length = 128)
    private String quarter;

    @Column(name = "area", length = 10)
    private String area;

    @Column(name = "bill_no", length = 16)
    private String billNo;

    @Column(name = "deposit", precision = 15, scale = 4)
    private BigDecimal deposit;

    @PrePersist
    protected void onCreate() {
        if (this.createdDate == null) this.createdDate = LocalDateTime.now();
        if (this.createdBy == null) this.createdBy = 1L;
    }
}