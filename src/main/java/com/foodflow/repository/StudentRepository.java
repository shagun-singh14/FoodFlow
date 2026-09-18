package com.foodflow.repository;

import com.foodflow.config.JPAUtil;
import com.foodflow.entity.StudentEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for StudentEntity demonstrating JPQL queries.
 */
public class StudentRepository implements GenericRepository<StudentEntity, Long> {

    @Override
    public StudentEntity save(StudentEntity student) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (student.getId() == null) {
                em.persist(student);
            } else {
                student = em.merge(student);
            }
            tx.commit();
            return student;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<StudentEntity> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(StudentEntity.class, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<StudentEntity> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<StudentEntity> query = em.createQuery("SELECT s FROM StudentEntity s ORDER BY s.registrationNumber ASC", StudentEntity.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<StudentEntity> findByRegistrationNumber(String regNo) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<StudentEntity> query = em.createQuery(
                    "SELECT s FROM StudentEntity s WHERE UPPER(s.registrationNumber) = UPPER(:regNo)", StudentEntity.class);
            query.setParameter("regNo", regNo.trim());
            List<StudentEntity> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public Optional<StudentEntity> findByEmail(String email) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<StudentEntity> query = em.createQuery(
                    "SELECT s FROM StudentEntity s WHERE LOWER(s.email) = LOWER(:email)", StudentEntity.class);
            query.setParameter("email", email.trim());
            List<StudentEntity> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
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
            StudentEntity s = em.find(StudentEntity.class, id);
            if (s != null) {
                em.remove(s);
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
    public void delete(StudentEntity entity) {
        if (entity != null && entity.getId() != null) {
            deleteById(entity.getId());
        }
    }

    @Override
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(s) FROM StudentEntity s", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
