package com.security.avis.repository;

import org.springframework.data.repository.CrudRepository;
import com.security.avis.entite.Validation;

import java.util.Optional;

public interface ValidationRepository extends CrudRepository<Validation, Integer> {

    Optional<Validation> findByCode(String code);
}
