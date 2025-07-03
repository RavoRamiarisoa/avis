package com.security.avis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.security.avis.entite.Avis;

public interface AvisRepository extends JpaRepository<Avis, Integer> {
}
