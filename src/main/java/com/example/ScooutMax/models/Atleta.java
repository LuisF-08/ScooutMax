package com.example.ScooutMax.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "atletas_cartola")
public class Atleta {
    
    @Id
    @JsonProperty("atleta_id")
    private Long atletaId;

    private String apelido;
    private String posicao;

    @JsonProperty("preco_num")
    private Double precoNum;

    @JsonProperty("variacao_num")
    private Double variacaoNum;

    @JsonProperty("media_num")
    private Double mediaNum;

    @JsonProperty("jogos_num")
    private Integer jogosNum;

    @JsonProperty("minimo_para_valorizar")
    private Double minimoParaValorizar;

    // Scouts mapeados de dentro do objeto aninhado "scout" via setters ou mapeamento customizado
    private Double cartaoAmarelo = 0.0;
    private Double desarme = 0.0;
    private Double faltaCometida = 0.0;
    private Double finalizacaoDefendida = 0.0;
    private Double finalizacaoFora = 0.0;
    private Double faltaSofrida = 0.0;
    private Double gol = 0.0;
    private Double assistencia = 0.0;
    private Double saldoGols = 0.0;
    private Double defesa = 0.0;
    private Double golsSofridos = 0.0;

    @ManyToOne
    @JoinColumn(name = "clube_id", nullable = false)
    private Clube clube;

    // Método auxiliar para popular os scouts vindos do Map do JSON
    public void setScoutsFromMap(java.util.Map<String, Object> scoutMap) {
        if (scoutMap == null) return;
        this.cartaoAmarelo = getDouble(scoutMap, "CA");
        this.desarme = getDouble(scoutMap, "DS");
        this.faltaCometida = getDouble(scoutMap, "FC");
        this.finalizacaoDefendida = getDouble(scoutMap, "FD");
        this.finalizacaoFora = getDouble(scoutMap, "FF");
        this.faltaSofrida = getDouble(scoutMap, "FS");
        this.gol = getDouble(scoutMap, "G");
        this.assistencia = getDouble(scoutMap, "A");
        this.saldoGols = getDouble(scoutMap, "SG");
        this.defesa = getDouble(scoutMap, "DE");
        this.golsSofridos = getDouble(scoutMap, "GS");
    }

    private Double getDouble(java.util.Map<String, Object> map, String key) {
        Object val = map.get(key);
        if (val instanceof Number) {
            return ((Number) val).doubleValue();
        }
        return 0.0;
    }

    // Getters e Setters básicos...
    public Long getAtletaId() { return atletaId; }
    public void setAtletaId(Long atletaId) { this.atletaId = atletaId; }
    public String getApelido() { return apelido; }
    public void setApelido(String apelido) { this.apelido = apelido; }
    public Double getPrecoNum() { return precoNum; }
    public void setPrecoNum(Double precoNum) { this.precoNum = precoNum; }
    public Double getMediaNum() { return mediaNum; }
    public void setMediaNum(Double mediaNum) { this.mediaNum = mediaNum; }
    public Clube getClube() { return clube; }
    public void setClube(Clube clube) { this.clube = clube; }
}
