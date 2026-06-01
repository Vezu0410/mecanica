package com.garageautobot.garagemautobot.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "veiculo_peca")
public class VeiculoPeca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "peca_id")
    private Peca peca;

    @Column(nullable = false)
    private int quantidadeUsada;

    public VeiculoPeca() {}

    public VeiculoPeca(Veiculo veiculo, Peca peca, int quantidadeUsada) {
        this.veiculo = veiculo;
        this.peca = peca;
        this.quantidadeUsada = quantidadeUsada;
    }

    public Long getId() { return id; }
    public Veiculo getVeiculo() { return veiculo; }
    public Peca getPeca() { return peca; }
    public int getQuantidadeUsada() { return quantidadeUsada; }

    public void setId(Long id) { this.id = id; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }
    public void setPeca(Peca peca) { this.peca = peca; }
    public void setQuantidadeUsada(int quantidadeUsada) { this.quantidadeUsada = quantidadeUsada; }
}
