
package com.example.ScooutMax.service;

import com.example.ScooutMax.models.Atleta;
import com.example.ScooutMax.models.Clube;
import com.example.ScooutMax.repository.AtletaRepository;
import com.example.ScooutMax.repository.ClubeRepository;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CartolaApiService {

    @Autowired
    private ClubeRepository clubeRepository;

    @Autowired
    private AtletaRepository atletaRepository;

    private final String CARTOLA_URL =
            "https://api.cartolafc.globo.com/atletas/mercado";

    public void importarAtletaEClube() {

        RestTemplate restTemplate = new RestTemplate();

        try{ 
            // configutando headers
            HttpHeaders headers = new  HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    CARTOLA_URL,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> resposta = response.getBody();
    
            if (resposta == null) return;
    
            // Processar e salvar os clubes
            Map<String, Map<String, Object>> clubesMap = (Map<String, Map<String, Object>>) resposta.get("clubes");
    
            if (clubesMap == null) return;
    
            for (Map.Entry<String, Map<String, Object>> entry : clubesMap.entrySet()) {
    
                Map<String, Object> dadosClube = entry.getValue();
    
                String sigla = (String) dadosClube.get("abreviacao");
                String nome = (String) dadosClube.get("nome");
    
                Clube clube = clubeRepository.findBySiglaCartola(sigla).orElse(new Clube(nome, sigla));
    
                clube.setNomeCsv(nome);
    
                clubeRepository.save(clube);
            }
    
            // Processar e salvar os atletas
            List<Map<String, Object>> atletasList = (List<Map<String, Object>>) resposta.get("atletas");
    
            if (atletasList == null) return;
    
            for (Map<String, Object> dadosAtleta : atletasList) {
    
                Long atletaId = ((Number) dadosAtleta
                        .get("atleta_id")).longValue();
    
                String apelido = (String) dadosAtleta.get("apelido");
    
                // Localizar o clube do atleta
                String clubeIdStr = String.valueOf(dadosAtleta.get("clube_id"));
    
                Map<String, Object> infoClubeJson = clubesMap.get(clubeIdStr);
    
                if (infoClubeJson == null) continue;
    
                String siglaClube = (String) infoClubeJson.get("abreviacao");
    
                Clube clube = clubeRepository
                        .findBySiglaCartola(siglaClube)
                        .orElse(null);
    
                if (clube == null) continue;
    
                // Mapear valores numéricos
                Double preco = ((Number) dadosAtleta.getOrDefault("preco_num", 0.0)).doubleValue();
    
                Double media = ((Number) dadosAtleta.getOrDefault("media_num", 0.0)).doubleValue();
    
                // Buscar atleta existente ou criar novo
                // Dentro do loop de atletas:
                Map<String, Object> scoutMap = (Map<String, Object>) dadosAtleta.get("scout");
                
                Atleta atleta = atletaRepository.findById(atletaId).orElse(new Atleta());
                atleta.setAtletaId(atletaId);
                atleta.setApelido(apelido);
                atleta.setPrecoNum(preco);
                atleta.setMediaNum(media);
                atleta.setClube(clube);
                atleta.setScoutsFromMap(scoutMap); // Preenche os scouts corretamente!
                
                atletaRepository.save(atleta);
        }
    } catch (Exception e){
        throw new Error
        ("Falha ao comunicar com a API do Cartola FC. Verifique se o mercado está aberto ou se há bloqueio de rede: " + e.getMessage(), e);
    }
    }
}
