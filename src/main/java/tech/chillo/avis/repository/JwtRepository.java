package tech.chillo.avis.repository;

import org.springframework.data.repository.CrudRepository;
import tech.chillo.avis.entite.Jwt;

public interface JwtRepository extends CrudRepository<Jwt, Integer> {

}
