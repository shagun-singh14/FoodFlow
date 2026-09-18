package com.foodflow.model;

import com.foodflow.model.enums.PaymentStatus;
import com.foodflow.model.interfaces.Exportable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model representing monthly billing, meal accounting, and payment status for students.
 */
public class Bill implements Exportable, Serializable {

    private static final long serialVersionUID = 1L;

    private Long billId;
    private Long studentId;
    private String studentName;
    private String registrationNumber;
    private String billingMonth;
    private int totalMeals;
    private BigDecimal amount;
    private PaymentStatus paymentStatus;
    private LocalDateTime generatedAt;
    private LocalDateTime paidAt;

    public Bill() {
        this.amount = BigDecimal.ZERO;
        this.paymentStatus = PaymentStatus.PENDING;
        this.generatedAt = LocalDateTime.now();
    }

    public Bill(Long billId, Long studentId, String billingMonth, int totalMeals, BigDecimal amount, PaymentStatus paymentStatus) {
        this();
        this.billId = billId;
        this.studentId = studentId;
        this.billingMonth = billingMonth;
        this.totalMeals = totalMeals;
        this.amount = amount != null ? amount : BigDecimal.ZERO;
        this.paymentStatus = paymentStatus != null ? paymentStatus : PaymentStatus.PENDING;
    }

    public void markPaid() {
        this.paymentStatus = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
    }

    @Override
    public String toFormattedText() {
        return String.format("""
                FOODFLOW MONTHLY BILL INVOICE
                ----------------------------------------
                Invoice ID  : #%d
                Student     : %s (%s)
                Month       : %s
                Total Meals : %d
                Total Dues  : ₹%.2f
                Status      : %s
                Generated   : %s
                Paid At     : %s
                ----------------------------------------
                """,
                billId != null ? billId : 0L,
                studentName != null ? studentName : "ID " + studentId,
                registrationNumber != null ? registrationNumber : "N/A",
                billingMonth, totalMeals, amount.doubleValue(),
                paymentStatus.getLabel(), generatedAt,
                paidAt != null ? paidAt.toString() : "Pending");
    }

    @Override
    public String toCsvRow() {
        return String.format("%d,%d,\"%s\",\"%s\",\"%s\",%d,%.2f,\"%s\",\"%s\",\"%s\"",
                billId != null ? billId : 0L,
                studentId,
                studentName != null ? studentName : "",
                registrationNumber != null ? registrationNumber : "",
                billingMonth,
                totalMeals,
                amount.doubleValue(),
                paymentStatus.name(),
                generatedAt,
                paidAt != null ? paidAt.toString() : "");
    }

    @Override
    public String getCsvHeader() {
        return "BillId,StudentId,StudentName,RegNo,BillingMonth,TotalMeals,Amount,PaymentStatus,GeneratedAt,PaidAt";
    }

    // Getters and Setters
    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(String billingMonth) {
        this.billingMonth = billingMonth;
    }

    public int getTotalMeals() {
        return totalMeals;
    }

    public void setTotalMeals(int totalMeals) {
        this.totalMeals = totalMeals;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    @Override
    public String toString() {
        return String.format("Bill[ID=%d, Student=%d, Month='%s', Meals=%d, Amount=₹%.2f, Status=%s]",
                billId, studentId, billingMonth, totalMeals, amount.doubleValue(), paymentStatus);
    }
}
