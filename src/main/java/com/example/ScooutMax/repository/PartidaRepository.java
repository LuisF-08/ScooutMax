package com.example.ScooutMax.repository;

import com.example.ScooutMax.models.Partidas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends JpaRepository<Partidas, Long> {
    
}
