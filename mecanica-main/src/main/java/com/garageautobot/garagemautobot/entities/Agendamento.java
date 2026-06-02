package com.garageautobot.garagemautobot.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cliente é obrigatório
    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    // Veículo é opcional: o cliente pode agendar sem o veículo estar cadastrado ainda.
    // Se for null, no momento de "carro chegou" o funcionário vincula/cadastra.
    @ManyToOne
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    // Data do agendamento
    @Column(name = "data_agendada", nullable = false)
    private LocalDate dataAgendada;

    // Período: manhã ou tarde
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodoAgendamento periodo;

    // Descrição do serviço solicitado / motivo
    @Column(name = "servico_solicitado", nullable = false, columnDefinition = "TEXT")
    private String servicoSolicitado;

    // Observações adicionais
    @Column(columnDefinition = "TEXT")
    private String observacoes;

    // Telefone de contato (pré-preenchido com o do cliente, mas editável)
    @Column(name = "telefone_contato")
    private String telefoneContato;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status = StatusAgendamento.AGENDADO;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    // Se virou OS, guarda o ID da OS gerada (para rastreabilidade)
    @Column(name = "os_gerada_id")
    private Long osGeradaId;

    // ── Construtores ──────────────────────────────────────────────
    public Agendamento() {}

    // ── Getters e Setters ─────────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public LocalDate getDataAgendada() { return dataAgendada; }
    public void setDataAgendada(LocalDate dataAgendada) { this.dataAgendada = dataAgendada; }

    public PeriodoAgendamento getPeriodo() { return periodo; }
    public void setPeriodo(PeriodoAgendamento periodo) { this.periodo = periodo; }

    public String getServicoSolicitado() { return servicoSolicitado; }
    public void setServicoSolicitado(String servicoSolicitado) { this.servicoSolicitado = servicoSolicitado; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getTelefoneContato() { return telefoneContato; }
    public void setTelefoneContato(String telefoneContato) { this.telefoneContato = telefoneContato; }

    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = status; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public Long getOsGeradaId() { return osGeradaId; }
    public void setOsGeradaId(Long osGeradaId) { this.osGeradaId = osGeradaId; }
}