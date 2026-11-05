package com.example.ScooutMax.repository;

import com.example.ScooutMax.models.Atleta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AtletaRepository extends JpaRepository<Atleta, Long>{
    
    // Busca todos os atletas de um clube pela sigla (ex: "FLA")
    List<Atleta> findByClubeSiglaCartola(String sigla);
}
