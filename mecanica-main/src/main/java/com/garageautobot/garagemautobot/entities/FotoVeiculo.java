package com.garageautobot.garagemautobot.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "fotos_veiculo")
public class FotoVeiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome do arquivo salvo em disco (UUID + extensão original)
    @Column(name = "nome_arquivo", nullable = false)
    private String nomeArquivo;

    // Legenda opcional que o mecânico pode adicionar
    @Column(name = "legenda")
    private String legenda;

    // Momento em que a foto foi registrada
    @Column(name = "data_registro", nullable = false)
    private LocalDateTime dataRegistro = LocalDateTime.now();

    // Tipo de momento: ENTRADA (quando chegou) ou SAIDA (quando entregou)
    @Enumerated(EnumType.STRING)
    @Column(name = "momento", nullable = false)
    private MomentoFoto momento = MomentoFoto.ENTRADA;

    // Veículo ao qual a foto pertence (sempre obrigatório)
    @ManyToOne(optional = false)
    @JoinColumn(name = "veiculo_id", nullable = false)
    private Veiculo veiculo;

    // OS à qual a foto pertence (opcional — pode ser foto do veículo sem OS)
    @ManyToOne
    @JoinColumn(name = "os_id")
    private OrdemServico ordemServico;

    // ── Construtores ──────────────────────────────────────────────
    public FotoVeiculo() {}

    public FotoVeiculo(String nomeArquivo, Veiculo veiculo, MomentoFoto momento) {
        this.nomeArquivo   = nomeArquivo;
        this.veiculo       = veiculo;
        this.momento       = momento;
        this.dataRegistro  = LocalDateTime.now();
    }

    // ── Getters e Setters ─────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }

    public String getLegenda() { return legenda; }
    public void setLegenda(String legenda) { this.legenda = legenda; }

    public LocalDateTime getDataRegistro() { return dataRegistro; }
    public void setDataRegistro(LocalDateTime dataRegistro) { this.dataRegistro = dataRegistro; }

    public MomentoFoto getMomento() { return momento; }
    public void setMomento(MomentoFoto momento) { this.momento = momento; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public OrdemServico getOrdemServico() { return ordemServico; }
    public void setOrdemServico(OrdemServico ordemServico) { this.ordemServico = ordemServico; }
}