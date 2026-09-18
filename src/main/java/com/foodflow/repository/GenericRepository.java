package com.foodflow.repository;

import java.util.List;
import java.util.Optional;

/**
 * Generic repository interface defining standard JPA CRUD contracts.
 *
 * @param <T>  Entity type
 * @param <ID> Primary key type
 */
public interface GenericRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    void delete(T entity);
    long count();
}
