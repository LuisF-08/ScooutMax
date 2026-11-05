package com.example.ScooutMax.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "partidas")
public class Partidas {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataPartida; // Data tratada do CSV

    @ManyToOne
    @JoinColumn(name = "mandante_id", nullable = false)
    private Clube mandante;

    @ManyToOne
    @JoinColumn(name = "visitante_id", nullable = false)
    private Clube visitante;

    private Integer golsMandante;  // FTHG do CSV
    private Integer golsVisitante; // FTAG do CSV

    private Integer chutesMandante;  // HS do CSV (Home Shots)
    private Integer chutesVisitante; // AS do CSV (Away Shots)

    // Construtores, Getters e Setters
    public Partidas() {}

    public Long getId() { return id; }
    public LocalDate getDataPartida() { return dataPartida; }
    public void setDataPartida(LocalDate dataPartida) { this.dataPartida = dataPartida; }
    public Clube getMandante() { return mandante; }
    public void setMandante(Clube mandante) { this.mandante = mandante; }
    public Clube getVisitante() { return visitante; }
    public void setVisitante(Clube visitante) { this.visitante = visitante; }
    public Integer getGolsMandante() { return golsMandante; }
    public void setGolsMandante(Integer golsMandante) { this.golsMandante = golsMandante; }
    public Integer getGolsVisitante() { return golsVisitante; }
    public void setGolsVisitante(Integer golsVisitante) { this.golsVisitante = golsVisitante; }
}
