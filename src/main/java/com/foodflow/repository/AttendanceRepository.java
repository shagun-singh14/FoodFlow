package com.foodflow.repository;

import com.foodflow.config.JPAUtil;
import com.foodflow.entity.AttendanceEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for AttendanceEntity with JPQL filtering and verification.
 */
public class AttendanceRepository implements GenericRepository<AttendanceEntity, Long> {

    @Override
    public AttendanceEntity save(AttendanceEntity attendance) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (attendance.getAttendanceId() == null) {
                em.persist(attendance);
            } else {
                attendance = em.merge(attendance);
            }
            tx.commit();
            return attendance;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<AttendanceEntity> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(AttendanceEntity.class, id));
        } finally {
            em.close();
        }
    }

    public List<AttendanceEntity> findByStudentId(Long studentId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AttendanceEntity> query = em.createQuery(
                    "SELECT a FROM AttendanceEntity a JOIN FETCH a.student s WHERE s.id = :studentId ORDER BY a.attendanceDate DESC", AttendanceEntity.class);
            query.setParameter("studentId", studentId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<AttendanceEntity> findByDate(LocalDate date) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AttendanceEntity> query = em.createQuery(
                    "SELECT a FROM AttendanceEntity a JOIN FETCH a.student s WHERE a.attendanceDate = :date", AttendanceEntity.class);
            query.setParameter("date", date);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existsByStudentAndDateAndMeal(Long studentId, LocalDate date, String mealType) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(a) FROM AttendanceEntity a WHERE a.student.id = :studentId AND a.attendanceDate = :date AND a.mealType = :mealType", Long.class);
            query.setParameter("studentId", studentId);
            query.setParameter("date", date);
            query.setParameter("mealType", mealType);
            return query.getSingleResult() > 0;
        } finally {
            em.close();
        }
    }

    @Override
    public List<AttendanceEntity> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM AttendanceEntity a JOIN FETCH a.student s ORDER BY a.attendanceDate DESC", AttendanceEntity.class).getResultList();
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
            AttendanceEntity a = em.find(AttendanceEntity.class, id);
            if (a != null) {
                em.remove(a);
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
    public void delete(AttendanceEntity entity) {
        if (entity != null && entity.getAttendanceId() != null) {
            deleteById(entity.getAttendanceId());
        }
    }

    @Override
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(a) FROM AttendanceEntity a", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
