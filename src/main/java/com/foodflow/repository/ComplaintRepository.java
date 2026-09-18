package com.foodflow.repository;

import com.foodflow.config.JPAUtil;
import com.foodflow.entity.ComplaintEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for ComplaintEntity with JPQL status queries.
 */
public class ComplaintRepository implements GenericRepository<ComplaintEntity, Long> {

    @Override
    public ComplaintEntity save(ComplaintEntity complaint) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (complaint.getComplaintId() == null) {
                em.persist(complaint);
            } else {
                complaint = em.merge(complaint);
            }
            tx.commit();
            return complaint;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<ComplaintEntity> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(ComplaintEntity.class, id));
        } finally {
            em.close();
        }
    }

    public List<ComplaintEntity> findByStatus(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ComplaintEntity> query = em.createQuery(
                    "SELECT c FROM ComplaintEntity c JOIN FETCH c.student s WHERE c.status = :status ORDER BY c.createdAt DESC", ComplaintEntity.class);
            query.setParameter("status", status);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<ComplaintEntity> findByStudentId(Long studentId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<ComplaintEntity> query = em.createQuery(
                    "SELECT c FROM ComplaintEntity c JOIN FETCH c.student s WHERE s.id = :studentId ORDER BY c.createdAt DESC", ComplaintEntity.class);
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<ComplaintEntity> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT c FROM ComplaintEntity c JOIN FETCH c.student s ORDER BY c.createdAt DESC", ComplaintEntity.class).getResultList();
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
            ComplaintEntity c = em.find(ComplaintEntity.class, id);
            if (c != null) {
                em.remove(c);
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
    public void delete(ComplaintEntity entity) {
        if (entity != null && entity.getComplaintId() != null) {
            deleteById(entity.getComplaintId());
        }
    }

    @Override
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(c) FROM ComplaintEntity c", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public long countByStatus(String status) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(c) FROM ComplaintEntity c WHERE c.status = :status", Long.class)
                    .setParameter("status", status)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }
}
