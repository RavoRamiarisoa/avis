package com.security.avis.repository;

import org.springframework.data.repository.CrudRepository;
import com.security.avis.entite.Utilisateur;

import java.util.Optional;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByEmail(String email);
}
