package com.foodflow.repository;

import com.foodflow.config.JPAUtil;
import com.foodflow.entity.BillEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for BillEntity with revenue calculations.
 */
public class BillRepository implements GenericRepository<BillEntity, Long> {

    @Override
    public BillEntity save(BillEntity bill) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (bill.getBillId() == null) {
                em.persist(bill);
            } else {
                bill = em.merge(bill);
            }
            tx.commit();
            return bill;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<BillEntity> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(BillEntity.class, id));
        } finally {
            em.close();
        }
    }

    public Optional<BillEntity> findByStudentAndMonth(Long studentId, String month) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<BillEntity> query = em.createQuery(
                    "SELECT b FROM BillEntity b JOIN FETCH b.student s WHERE s.id = :studentId AND b.billingMonth = :month", BillEntity.class);
            query.setParameter("studentId", studentId);
            query.setParameter("month", month);
            List<BillEntity> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public List<BillEntity> findByStudentId(Long studentId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<BillEntity> query = em.createQuery(
                    "SELECT b FROM BillEntity b JOIN FETCH b.student s WHERE s.id = :studentId ORDER BY b.generatedAt DESC", BillEntity.class);
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public BigDecimal calculateTotalCollectedRevenue() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            BigDecimal sum = em.createQuery(
                    "SELECT SUM(b.amount) FROM BillEntity b WHERE b.paymentStatus = 'PAID'", BigDecimal.class).getSingleResult();
            return (sum != null) ? sum : BigDecimal.ZERO;
        } finally {
            em.close();
        }
    }

    @Override
    public List<BillEntity> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT b FROM BillEntity b JOIN FETCH b.student s ORDER BY b.generatedAt DESC", BillEntity.class).getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            BillEntity b = em.find(BillEntity.class, id);
            if (b != null) {
                em.remove(b);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(BillEntity entity) {
        if (entity != null && entity.getBillId() != null) {
            deleteById(entity.getBillId());
        }
    }

    @Override
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(b) FROM BillEntity b", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
