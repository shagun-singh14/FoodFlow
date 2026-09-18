package com.foodflow;

import com.foodflow.model.Bill;
import com.foodflow.model.enums.PaymentStatus;
import com.foodflow.service.BillingService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BillingServiceTest {

    private static BillingService billingService;

    @BeforeAll
    public static void setup() {
        billingService = new BillingService();
    }

    @Test
    public void testGenerateMonthlyBill() throws Exception {
        Bill bill = billingService.generateMonthlyBill(3L, "September 2026", 2026, 9);
        assertNotNull(bill);
        assertNotNull(bill.getAmount());
        assertTrue(bill.getTotalMeals() >= 0);
    }

    @Test
    public void testTotalRevenueCalculation() throws Exception {
        BigDecimal rev = billingService.calculateTotalRevenue();
        assertNotNull(rev);
        assertTrue(rev.doubleValue() >= 0);
    }
}
