package com.foodflow.repository;

import com.foodflow.config.JPAUtil;
import com.foodflow.entity.FeedbackEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for FeedbackEntity with JPQL aggregation and low-rated query filters.
 */
public class FeedbackRepository implements GenericRepository<FeedbackEntity, Long> {

    @Override
    public FeedbackEntity save(FeedbackEntity feedback) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (feedback.getFeedbackId() == null) {
                em.persist(feedback);
            } else {
                feedback = em.merge(feedback);
            }
            tx.commit();
            return feedback;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<FeedbackEntity> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(FeedbackEntity.class, id));
        } finally {
            em.close();
        }
    }

    public List<FeedbackEntity> findByMealType(String mealType) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<FeedbackEntity> query = em.createQuery(
                    "SELECT f FROM FeedbackEntity f JOIN FETCH f.student s WHERE f.mealType = :mealType ORDER BY f.feedbackDate DESC", FeedbackEntity.class);
            query.setParameter("mealType", mealType);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<FeedbackEntity> findLowRatedMeals(int maxRating) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<FeedbackEntity> query = em.createQuery(
                    "SELECT f FROM FeedbackEntity f JOIN FETCH f.student s WHERE f.rating <= :maxRating ORDER BY f.rating ASC, f.feedbackDate DESC", FeedbackEntity.class);
            query.setParameter("maxRating", maxRating);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public double calculateAverageRating() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Double avg = em.createQuery("SELECT AVG(f.rating) FROM FeedbackEntity f", Double.class).getSingleResult();
            return (avg != null) ? avg : 0.0;
        } finally {
            em.close();
        }
    }

    public double calculateAverageRatingForMeal(String mealType) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            Double avg = em.createQuery("SELECT AVG(f.rating) FROM FeedbackEntity f WHERE f.mealType = :mealType", Double.class)
                    .setParameter("mealType", mealType)
                    .getSingleResult();
            return (avg != null) ? avg : 0.0;
        } finally {
            em.close();
        }
    }

    @Override
    public List<FeedbackEntity> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT f FROM FeedbackEntity f JOIN FETCH f.student s ORDER BY f.feedbackDate DESC", FeedbackEntity.class).getResultList();
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
            FeedbackEntity f = em.find(FeedbackEntity.class, id);
            if (f != null) {
                em.remove(f);
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
    public void delete(FeedbackEntity entity) {
        if (entity != null && entity.getFeedbackId() != null) {
            deleteById(entity.getFeedbackId());
        }
    }

    @Override
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(f) FROM FeedbackEntity f", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
