package com.foodflow.repository;

import com.foodflow.config.JPAUtil;
import com.foodflow.entity.MenuEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for MenuEntity with JPQL date range queries.
 */
public class MenuRepository implements GenericRepository<MenuEntity, Long> {

    @Override
    public MenuEntity save(MenuEntity menu) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (menu.getMenuId() == null) {
                em.persist(menu);
            } else {
                menu = em.merge(menu);
            }
            tx.commit();
            return menu;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<MenuEntity> findById(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(MenuEntity.class, id));
        } finally {
            em.close();
        }
    }

    public Optional<MenuEntity> findByDate(LocalDate date) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<MenuEntity> query = em.createQuery(
                    "SELECT m FROM MenuEntity m WHERE m.menuDate = :date", MenuEntity.class);
            query.setParameter("date", date);
            List<MenuEntity> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public List<MenuEntity> findBetweenDates(LocalDate start, LocalDate end) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<MenuEntity> query = em.createQuery(
                    "SELECT m FROM MenuEntity m WHERE m.menuDate >= :start AND m.menuDate <= :end ORDER BY m.menuDate ASC", MenuEntity.class);
            query.setParameter("start", start);
            query.setParameter("end", end);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<MenuEntity> findAll() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT m FROM MenuEntity m ORDER BY m.menuDate DESC", MenuEntity.class).getResultList();
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
            MenuEntity m = em.find(MenuEntity.class, id);
            if (m != null) {
                em.remove(m);
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
    public void delete(MenuEntity entity) {
        if (entity != null && entity.getMenuId() != null) {
            deleteById(entity.getMenuId());
        }
    }

    @Override
    public long count() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT COUNT(m) FROM MenuEntity m", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }
}
