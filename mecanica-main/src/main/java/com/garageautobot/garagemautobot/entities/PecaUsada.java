package com.garageautobot.garagemautobot.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pecas_usadas")
public class PecaUsada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quantidade da peça usada neste veículo
    @Column(nullable = false)
    private int quantidadeUsada;

    @Column(nullable = false)
    private LocalDateTime dataUso = LocalDateTime.now();

    // Relação com o veículo
    @ManyToOne
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    // Relação com a peça
    @ManyToOne
    @JoinColumn(name = "peca_id", nullable = false)
    private Peca peca;

    // Valor unitário na data (caso o preço mude no estoque depois)
    @Column(nullable = false)
    private double valorUnitarioAplicado;

    public PecaUsada() {}

    public PecaUsada(Veiculo veiculo, Peca peca, int quantidadeUsada, double valorUnitarioAplicado) {
        this.veiculo = veiculo;
        this.peca = peca;
        this.quantidadeUsada = quantidadeUsada;
        this.valorUnitarioAplicado = valorUnitarioAplicado;
        this.dataUso = LocalDateTime.now();
    }

    // Getters e setters
    public Long getId() { return id; }

    public int getQuantidadeUsada() { return quantidadeUsada; }
    public void setQuantidadeUsada(int quantidadeUsada) { this.quantidadeUsada = quantidadeUsada; }

    public LocalDateTime getDataUso() { return dataUso; }
    public void setDataUso(LocalDateTime dataUso) { this.dataUso = dataUso; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public Peca getPeca() { return peca; }
    public void setPeca(Peca peca) { this.peca = peca; }

    public double getValorUnitarioAplicado() { return valorUnitarioAplicado; }
    public void setValorUnitarioAplicado(double valorUnitarioAplicado) { this.valorUnitarioAplicado = valorUnitarioAplicado; }

    public double getValorTotal() {
        return quantidadeUsada * valorUnitarioAplicado;
    }
}
