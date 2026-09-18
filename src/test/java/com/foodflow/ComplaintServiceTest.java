package com.foodflow;

import com.foodflow.model.Complaint;
import com.foodflow.model.enums.ComplaintCategory;
import com.foodflow.model.enums.ComplaintStatus;
import com.foodflow.service.ComplaintService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ComplaintServiceTest {

    private static ComplaintService complaintService;

    @BeforeAll
    public static void setup() {
        complaintService = new ComplaintService();
    }

    @Test
    public void testFileAndResolveComplaint() throws Exception {
        Complaint c = complaintService.fileComplaint(3L, ComplaintCategory.FOOD_QUALITY, "Test complaint about soup temperature.");
        assertNotNull(c.getComplaintId());
        assertEquals(ComplaintStatus.OPEN, c.getStatus());

        complaintService.updateComplaintStatus(c.getComplaintId(), ComplaintStatus.RESOLVED, "Temperature rectified.");
        Complaint updated = complaintService.getComplaintById(c.getComplaintId());
        assertEquals(ComplaintStatus.RESOLVED, updated.getStatus());
        assertEquals("Temperature rectified.", updated.getAdminRemarks());
    }
}
