package com.example.ScooutMax.models;

import jakarta.persistence.*;
import java.util.List;

@Entity 
@Table(name = "clubes")
public class Clube{
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nomeCsv; // ex: Palmeiras, Flamengo, etc...

    @Column(unique = true, nullable =  false)
    private  String siglaCartola; // ex: FLA, PAL, etc...

    @OneToMany(mappedBy = "clube", cascade = CascadeType.ALL)
    private List<Atleta> atletas;

    // Contrutors, GETTERS e SETTERS
    public  Clube() {}

    public  Clube(String nomeCsv, String siglaCartola){
        this.nomeCsv = nomeCsv;
        this.siglaCartola = siglaCartola;
    }

    public Long getId() { return id; }
    public String getNomeCsv() { return nomeCsv; }
    public void setNomeCsv(String nomeCsv) { this.nomeCsv = nomeCsv; }
    public String getSiglaCartola() { return siglaCartola; }
    public void setSiglaCartola(String siglaCartola) { this.siglaCartola = siglaCartola; }
    public List<Atleta> getAtletas() { return atletas; }
    public void setAtletas(List<Atleta> atletas) { this.atletas = atletas; }

}
