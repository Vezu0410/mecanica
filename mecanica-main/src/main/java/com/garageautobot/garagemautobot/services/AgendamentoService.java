package com.garageautobot.garagemautobot.services;

import com.garageautobot.garagemautobot.entities.*;
import com.garageautobot.garagemautobot.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ClienteRepository    clienteRepository;
    private final VeiculoRepository    veiculoRepository;
    private final OrdemServicoService  ordemServicoService;

    @Autowired
    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                               ClienteRepository clienteRepository,
                               VeiculoRepository veiculoRepository,
                               OrdemServicoService ordemServicoService) {
        this.agendamentoRepository = agendamentoRepository;
        this.clienteRepository     = clienteRepository;
        this.veiculoRepository     = veiculoRepository;
        this.ordemServicoService   = ordemServicoService;
    }

    // ── LISTAGENS ─────────────────────────────────────────────────

    public List<Agendamento> listarPorData(LocalDate data) {
        return agendamentoRepository.findByDataAgendadaOrderByPeriodoAsc(data);
    }

    public List<Agendamento> listarEntreDatas(LocalDate inicio, LocalDate fim) {
        return agendamentoRepository
                .findByDataAgendadaBetweenOrderByDataAgendadaAscPeriodoAsc(inicio, fim);
    }

    public List<Agendamento> listarFuturos() {
        return agendamentoRepository
                .findByStatusAndDataAgendadaGreaterThanEqualOrderByDataAgendadaAsc(
                        StatusAgendamento.AGENDADO, LocalDate.now());
    }

    public Optional<Agendamento> findById(Long id) {
        return agendamentoRepository.findById(id);
    }

    // Conta quantos agendamentos ativos há num período (para mostrar lotação)
    public long contarNoPeriodo(LocalDate data, PeriodoAgendamento periodo) {
        return agendamentoRepository.countByDataAgendadaAndPeriodoAndStatus(
                data, periodo, StatusAgendamento.AGENDADO);
    }

    // ── CRIAR AGENDAMENTO ─────────────────────────────────────────

    @Transactional
    public Agendamento criar(Long clienteId,
                             Long veiculoId,
                             LocalDate data,
                             PeriodoAgendamento periodo,
                             String servicoSolicitado,
                             String observacoes,
                             String telefoneContato) {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + clienteId));

        Agendamento ag = new Agendamento();
        ag.setCliente(cliente);

        // Veículo é opcional
        if (veiculoId != null) {
            Veiculo veiculo = veiculoRepository.findById(veiculoId).orElse(null);
            ag.setVeiculo(veiculo);
        }

        ag.setDataAgendada(data);
        ag.setPeriodo(periodo);
        ag.setServicoSolicitado(servicoSolicitado);
        ag.setObservacoes(observacoes);

        // Telefone: usa o informado ou pega do cliente
        if (telefoneContato != null && !telefoneContato.isBlank()) {
            ag.setTelefoneContato(telefoneContato);
        } else {
            ag.setTelefoneContato(cliente.getTelefone());
        }

        ag.setStatus(StatusAgendamento.AGENDADO);

        return agendamentoRepository.save(ag);
    }

    // ── CARRO CHEGOU → VIRA OS ────────────────────────────────────

    /**
     * Converte o agendamento em uma Ordem de Serviço.
     * Se o agendamento já tem veículo vinculado, abre a OS direto.
     * Se não tem, é preciso informar o veiculoId (escolhido/cadastrado na hora).
     */
    @Transactional
    public OrdemServico converterEmOS(Long agendamentoId, Long veiculoId) {
        Agendamento ag = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado: " + agendamentoId));

        if (ag.getStatus() != StatusAgendamento.AGENDADO) {
            throw new RuntimeException("Este agendamento já foi processado (status: "
                    + ag.getStatus().getDescricao() + ").");
        }

        // Decide qual veículo usar
        Long veiculoFinal = veiculoId != null ? veiculoId
                : (ag.getVeiculo() != null ? ag.getVeiculo().getId() : null);

        if (veiculoFinal == null) {
            throw new RuntimeException("É necessário informar o veículo para abrir a OS.");
        }

        // Abre a OS reaproveitando o módulo existente
        OrdemServico os = ordemServicoService.abrirOS(
                veiculoFinal,
                ag.getServicoSolicitado(),
                ag.getObservacoes());

        // Marca o agendamento como atendido
        ag.setStatus(StatusAgendamento.COMPARECEU);
        ag.setOsGeradaId(os.getId());
        agendamentoRepository.save(ag);

        return os;
    }

    // ── MARCAR COMO NÃO COMPARECEU ────────────────────────────────

    @Transactional
    public void marcarNaoCompareceu(Long agendamentoId) {
        Agendamento ag = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado: " + agendamentoId));
        ag.setStatus(StatusAgendamento.NAO_COMPARECEU);
        agendamentoRepository.save(ag);
    }

    // ── CANCELAR ──────────────────────────────────────────────────

    @Transactional
    public void cancelar(Long agendamentoId) {
        Agendamento ag = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado: " + agendamentoId));
        ag.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(ag);
    }

    // ── REABRIR (volta para AGENDADO) ─────────────────────────────

    @Transactional
    public void reabrir(Long agendamentoId) {
        Agendamento ag = agendamentoRepository.findById(agendamentoId)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado: " + agendamentoId));
        ag.setStatus(StatusAgendamento.AGENDADO);
        ag.setOsGeradaId(null);
        agendamentoRepository.save(ag);
    }
}