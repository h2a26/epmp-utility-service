package org.mpay.utilityservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_electricity_bill", schema = "prepost")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailedElectricityBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "failure_reason")
    private String failureReason;

    private String ledgerNo;
    private String consumerNo;
    private String meterNo;
    private String consumerName;
    private String address;
    private String billCode;
    private String billDueDate;
    private String usedUnit;
    private String billAmount;
    private String serviceCharges;
    private String horsePowerCharges;
    private String discount;
    private String lastBalance;
    private String totalBillAmount;
    private String debtBalance;
    private String installationFee;
    private String grandTotalAmount;
    private String township;
    private String terrifCode;
    private String readingDate;
    private String previousUnit;
    private String currentUnit;
    private String houseNo;
    private String road;
    private String quarter;
    private String area;
    private String billNo;
    private String deposit;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }
}