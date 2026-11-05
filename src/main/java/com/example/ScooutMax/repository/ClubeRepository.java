package com.example.ScooutMax.repository;

import com.example.ScooutMax.models.Clube;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ClubeRepository extends JpaRepository<Clube, Long>{

    Optional<Clube> findBySiglaCartola(String siglaCartola);
    Optional<Clube> findByNomeCsv(String nomeCsv);

}