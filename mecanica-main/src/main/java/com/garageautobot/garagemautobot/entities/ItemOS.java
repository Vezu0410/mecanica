package com.garageautobot.garagemautobot.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "itens_os")
public class ItemOS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "os_id", nullable = false)
    private OrdemServico ordemServico;

    @ManyToOne(optional = false)
    @JoinColumn(name = "peca_id", nullable = false)
    private Peca peca;

    @Column(nullable = false)
    private int quantidade;

    // Preço unitário travado no momento do uso (independente de mudanças futuras)
    @Column(name = "preco_unitario_aplicado", nullable = false)
    private double precoUnitarioAplicado;

    // ── Construtores ──────────────────────────────────────────────
    public ItemOS() {}

    public ItemOS(OrdemServico ordemServico, Peca peca, int quantidade) {
        this.ordemServico = ordemServico;
        this.peca = peca;
        this.quantidade = quantidade;
        this.precoUnitarioAplicado = peca.getPrecoUnitario();
    }

    // ── Cálculo ───────────────────────────────────────────────────
    public double getValorTotal() {
        return quantidade * precoUnitarioAplicado;
    }

    // ── Getters e Setters ─────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public OrdemServico getOrdemServico() { return ordemServico; }
    public void setOrdemServico(OrdemServico ordemServico) { this.ordemServico = ordemServico; }

    public Peca getPeca() { return peca; }
    public void setPeca(Peca peca) { this.peca = peca; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoUnitarioAplicado() { return precoUnitarioAplicado; }
    public void setPrecoUnitarioAplicado(double precoUnitarioAplicado) { this.precoUnitarioAplicado = precoUnitarioAplicado; }
}