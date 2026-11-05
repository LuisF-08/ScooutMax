package com.example.ScooutMax.service;

import com.example.ScooutMax.models.Clube;
import com.example.ScooutMax.models.Partidas;
import com.example.ScooutMax.repository.AtletaRepository;
import com.example.ScooutMax.repository.ClubeRepository;
import com.example.ScooutMax.repository.PartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CsvImportService {

    private static final Logger log = LoggerFactory.getLogger(CsvImportService.class);

    private static final String CSV_URL = "https://football-data.co.uk/new/BRA.csv";
    private static final DateTimeFormatter DATA_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Layout do BRA.csv: Country,League,Season,Date,Time,Home,Away,HG,AG,Res,...
    private static final int COL_DATE = 3;
    private static final int COL_HOME = 5;
    private static final int COL_AWAY = 6;
    private static final int COL_HG = 7;
    private static final int COL_AG = 8;
    private static final int MIN_COLUNAS = 9;

    @Autowired
    private ClubeRepository clubeRepository;

    @Autowired
    private PartidaRepository partidaRepository;

    @Transactional
    public void importarCsvHistorico() {
        Map<String, Clube> cacheClubes = new HashMap<>();
        Set<String> clubesNaoEncontrados = new HashSet<>();
        List<Partidas> partidas = new ArrayList<>();
        int ignoradas = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(URI.create(CSV_URL).toURL().openStream(), StandardCharsets.UTF_8))) {

            reader.readLine(); // cabeçalho (o BOM fica nele, então não afeta os dados)

            String linha;
            int numeroLinha = 1;

            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                if (linha.isBlank()) continue;

                String[] c = linha.split(",", -1); // -1 mantém colunas vazias no final
                if (c.length < MIN_COLUNAS) {
                    ignoradas++;
                    continue;
                }

                String homeTeam = c[COL_HOME].trim();
                String awayTeam = c[COL_AWAY].trim();
                String golsHomeStr = c[COL_HG].trim();
                String golsAwayStr = c[COL_AG].trim();

                // Jogo sem placar (não realizado / dado faltando): ignora
                if (golsHomeStr.isEmpty() || golsAwayStr.isEmpty()) {
                    ignoradas++;
                    continue;
                }

                Clube mandante = buscarClube(homeTeam, cacheClubes, clubesNaoEncontrados);
                Clube visitante = buscarClube(awayTeam, cacheClubes, clubesNaoEncontrados);
                if (mandante == null || visitante == null) {
                    ignoradas++;
                    continue;
                }

                try {
                    Partidas partida = new Partidas();
                    partida.setMandante(mandante);
                    partida.setVisitante(visitante);
                    partida.setGolsMandante(Integer.parseInt(golsHomeStr));
                    partida.setGolsVisitante(Integer.parseInt(golsAwayStr));
                    partida.setDataPartida(LocalDate.parse(c[COL_DATE].trim(), DATA_FMT));
                    partidas.add(partida);
                } catch (Exception e) {
                    log.warn("Linha {} ignorada ({}): {}", numeroLinha, e.getMessage(), linha);
                    ignoradas++;
                }
            }

            partidaRepository.saveAll(partidas);

            log.info("Importação concluída: {} partidas salvas, {} linhas ignoradas.",
                    partidas.size(), ignoradas);
            if (!clubesNaoEncontrados.isEmpty()) {
                log.warn("Clubes do CSV sem correspondência em nomeCsv: {}", clubesNaoEncontrados);
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao importar CSV: " + e.getMessage(), e);
        }
    }

    private Clube buscarClube(String nomeCsv, Map<String, Clube> cache, Set<String> naoEncontrados) {
        if (cache.containsKey(nomeCsv)) return cache.get(nomeCsv);
        Clube clube = clubeRepository.findByNomeCsv(nomeCsv).orElse(null);
        cache.put(nomeCsv, clube);
        if (clube == null) naoEncontrados.add(nomeCsv);
        return clube;
    }
}