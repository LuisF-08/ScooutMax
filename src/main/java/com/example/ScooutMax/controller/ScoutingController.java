package com.example.ScooutMax.controller;

import com.example.ScooutMax.service.CartolaApiService;
import com.example.ScooutMax.service.CsvImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController 
@RequestMapping("/api/scouting")
public class ScoutingController {
    
    @Autowired
    private CartolaApiService cartolaApiService;

    @Autowired 
    private CsvImportService csvImportService;

    // Endpoint para disparar a importação da API do Cartola
    @PostMapping("/importar/cartola")
    public ResponseEntity<String> importarCartola() {
        try{
            cartolaApiService.importarAtletaEClube();
            return ResponseEntity.ok("Atletas e Clubes do Cartola importados com sucesso!");
        } catch( Exception e){
            return ResponseEntity.internalServerError().body("Erro: " + e.getMessage());
        }
    }

    // Endpoint para disparar a importação do CSV histórico
    @PostMapping ("/importar/csv")
    public ResponseEntity<String> importarCsv() {
        try{
            csvImportService.importarCsvHistorico();
            return ResponseEntity.ok("Histórico de partidas do CSV importados com sucesso!!!");
        } catch( Exception e){
            return ResponseEntity.internalServerError().body(("Erro: " +e.getMessage()));
        }
    }


}
