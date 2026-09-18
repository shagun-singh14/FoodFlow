package com.foodflow.model.interfaces;

/**
 * Interface contract for entities/users capable of credential validation and role identification.
 */
public interface Authenticatable {
    String getEmail();
    String getPasswordHash();
    boolean authenticate(String plainPassword);
}
