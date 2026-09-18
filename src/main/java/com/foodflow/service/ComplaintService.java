package com.foodflow.service;

import com.foodflow.dao.ComplaintJDBCDAO;
import com.foodflow.exception.DatabaseException;
import com.foodflow.exception.FoodFlowException;
import com.foodflow.exception.InvalidComplaintException;
import com.foodflow.model.Complaint;
import com.foodflow.model.enums.ComplaintCategory;
import com.foodflow.model.enums.ComplaintStatus;
import com.foodflow.util.ValidationUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service managing grievance filing, tracking, and admin resolution workflows.
 */
public class ComplaintService {

    private final ComplaintJDBCDAO complaintDAO;

    public ComplaintService() {
        this.complaintDAO = new ComplaintJDBCDAO();
    }

    public Complaint fileComplaint(Long studentId, ComplaintCategory category, String description) throws FoodFlowException {
        ValidationUtil.validateNotEmpty(description, "Complaint Description");
        if (description.trim().length() < 10) {
            throw new InvalidComplaintException("Complaint description must provide sufficient detail (at least 10 characters).");
        }

        Complaint complaint = new Complaint(null, studentId, category, description.trim());
        return complaintDAO.createComplaint(complaint);
    }

    public List<Complaint> getAllComplaints() throws DatabaseException {
        return complaintDAO.findAll();
    }

    public List<Complaint> getComplaintsByStudent(Long studentId) throws DatabaseException {
        return complaintDAO.findByStudent(studentId);
    }

    public Complaint getComplaintById(Long complaintId) throws FoodFlowException {
        return complaintDAO.findById(complaintId);
    }

    public void updateComplaintStatus(Long complaintId, ComplaintStatus status, String adminRemarks) throws FoodFlowException {
        complaintDAO.updateStatus(complaintId, status, adminRemarks);
    }

    public Map<ComplaintCategory, Integer> getComplaintsByCategoryCount() throws DatabaseException {
        List<Complaint> all = complaintDAO.findAll();
        Map<ComplaintCategory, Integer> counts = new HashMap<>();
        for (ComplaintCategory cat : ComplaintCategory.values()) {
            counts.put(cat, 0);
        }
        for (Complaint c : all) {
            counts.put(c.getCategory(), counts.getOrDefault(c.getCategory(), 0) + 1);
        }
        return counts;
    }

    public int getOpenComplaintsCount() throws DatabaseException {
        List<Complaint> all = complaintDAO.findAll();
        int open = 0;
        for (Complaint c : all) {
            if (c.getStatus() == ComplaintStatus.OPEN || c.getStatus() == ComplaintStatus.IN_PROGRESS) {
                open++;
            }
        }
        return open;
    }
}
