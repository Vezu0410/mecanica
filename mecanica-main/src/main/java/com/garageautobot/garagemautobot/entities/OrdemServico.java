package com.garageautobot.garagemautobot.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordens_servico")
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Número legível para exibição: OS-0001, OS-0002, etc.
    @Column(name = "numero_os", nullable = false, unique = true)
    private String numeroOS;

    // Veículo atendido (sempre obrigatório — pode ser criado junto ou vinculado)
    @ManyToOne(optional = false)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    // Descrição do problema relatado pelo cliente
    @Column(name = "descricao_problema", nullable = false, columnDefinition = "TEXT")
    private String descricaoProblema;

    // Observações internas do mecânico
    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOS status = StatusOS.ABERTA;

    // Mão de obra: null = não cobrado, valor > 0 = cobrado
    @Column(name = "valor_mao_obra")
    private Double valorMaoObra;

    // Percentual de mão de obra sobre o total de peças (alternativa ao valor fixo)
    @Column(name = "percentual_mao_obra")
    private Double percentualMaoObra;

    // Indica se usa percentual (true) ou valor fixo (false)
    @Column(name = "mao_obra_por_percentual")
    private Boolean maoObraPorPercentual = false;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    // Peças usadas nesta OS
    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemOS> itens = new ArrayList<>();

    // ── Construtores ──────────────────────────────────────────────
    public OrdemServico() {}

    public OrdemServico(String numeroOS, Veiculo veiculo, String descricaoProblema) {
        this.numeroOS = numeroOS;
        this.veiculo = veiculo;
        this.descricaoProblema = descricaoProblema;
        this.dataAbertura = LocalDateTime.now();
        this.status = StatusOS.ABERTA;
    }

    // ── Métodos de cálculo ────────────────────────────────────────

    /** Soma do valor de todas as peças (quantidade × preço unitário aplicado) */
    public double getTotalPecas() {
        return itens.stream()
                .mapToDouble(ItemOS::getValorTotal)
                .sum();
    }

    /** Valor calculado de mão de obra */
    public double getValorMaoObraCalculado() {
        if (Boolean.TRUE.equals(maoObraPorPercentual) && percentualMaoObra != null) {
            return getTotalPecas() * (percentualMaoObra / 100.0);
        }
        return valorMaoObra != null ? valorMaoObra : 0.0;
    }

    /** Total geral da OS */
    public double getTotalGeral() {
        return getTotalPecas() + getValorMaoObraCalculado();
    }

    // ── Getters e Setters ─────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroOS() { return numeroOS; }
    public void setNumeroOS(String numeroOS) { this.numeroOS = numeroOS; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public String getDescricaoProblema() { return descricaoProblema; }
    public void setDescricaoProblema(String descricaoProblema) { this.descricaoProblema = descricaoProblema; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public StatusOS getStatus() { return status; }
    public void setStatus(StatusOS status) { this.status = status; }

    public Double getValorMaoObra() { return valorMaoObra; }
    public void setValorMaoObra(Double valorMaoObra) { this.valorMaoObra = valorMaoObra; }

    public Double getPercentualMaoObra() { return percentualMaoObra; }
    public void setPercentualMaoObra(Double percentualMaoObra) { this.percentualMaoObra = percentualMaoObra; }

    public Boolean getMaoObraPorPercentual() { return maoObraPorPercentual; }
    public void setMaoObraPorPercentual(Boolean maoObraPorPercentual) { this.maoObraPorPercentual = maoObraPorPercentual; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDate getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDate dataConclusao) { this.dataConclusao = dataConclusao; }

    public List<ItemOS> getItens() { return itens; }
    public void setItens(List<ItemOS> itens) { this.itens = itens; }
}