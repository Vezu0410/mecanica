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
public class OrdemServicoService {

    private final OrdemServicoRepository osRepository;
    private final ItemOSRepository itemOSRepository;
    private final PecaRepository pecaRepository;
    private final VeiculoRepository veiculoRepository;
    private final ClienteRepository clienteRepository;

    @Autowired
    public OrdemServicoService(OrdemServicoRepository osRepository,
                               ItemOSRepository itemOSRepository,
                               PecaRepository pecaRepository,
                               VeiculoRepository veiculoRepository,
                               ClienteRepository clienteRepository) {
        this.osRepository       = osRepository;
        this.itemOSRepository   = itemOSRepository;
        this.pecaRepository     = pecaRepository;
        this.veiculoRepository  = veiculoRepository;
        this.clienteRepository  = clienteRepository;
    }

    // ── LISTAGENS ─────────────────────────────────────────────────

    public List<OrdemServico> findAll() {
        return osRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getDataAbertura().compareTo(a.getDataAbertura()))
                .toList();
    }

    public Optional<OrdemServico> findById(Long id) {
        return osRepository.findById(id);
    }

    public List<OrdemServico> findByStatus(StatusOS status) {
        return osRepository.findByStatus(status);
    }

    public List<OrdemServico> buscarPorTermo(String termo) {
        if (termo == null || termo.isBlank()) return findAll();
        return osRepository.buscarPorTermo(termo);
    }

    // ── GERAÇÃO DE NÚMERO ─────────────────────────────────────────

    /** Gera número sequencial no formato OS-0001 */
    public String gerarNumeroOS() {
        long proximoId = osRepository.findMaxId() + 1;
        return String.format("OS-%04d", proximoId);
    }

    // ── ABERTURA DE OS COM VEÍCULO EXISTENTE ──────────────────────

    @Transactional
    public OrdemServico abrirOS(Long veiculoId,
                                String descricaoProblema,
                                String observacoes) {

        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado: " + veiculoId));

        OrdemServico os = new OrdemServico(gerarNumeroOS(), veiculo, descricaoProblema);
        os.setObservacoes(observacoes);

        // Atualiza status do veículo automaticamente
        veiculo.setStatus(StatusVeiculo.EM_MANUTENCAO);
        veiculoRepository.save(veiculo);

        return osRepository.save(os);
    }

    // ── ABERTURA DE OS CRIANDO VEÍCULO JUNTO ─────────────────────

    @Transactional
    public OrdemServico abrirOSComNovoVeiculo(Long clienteId,
                                               String marca,
                                               String modelo,
                                               String ano,
                                               String placa,
                                               String descricaoProblema,
                                               String observacoes) {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado: " + clienteId));

        // Verifica placa duplicada
        if (veiculoRepository.findByPlacaContainingIgnoreCase(placa.trim())
                .stream().anyMatch(v -> v.getPlaca().equalsIgnoreCase(placa.trim()))) {
            throw new RuntimeException("Já existe um veículo cadastrado com a placa: " + placa);
        }

        Veiculo veiculo = new Veiculo(marca, modelo, ano, placa.toUpperCase(), cliente);
        veiculo.setStatus(StatusVeiculo.EM_MANUTENCAO);
        veiculoRepository.save(veiculo);

        OrdemServico os = new OrdemServico(gerarNumeroOS(), veiculo, descricaoProblema);
        os.setObservacoes(observacoes);

        return osRepository.save(os);
    }

    // ── ADICIONAR PEÇA À OS ───────────────────────────────────────

    @Transactional
    public void adicionarItem(Long osId, Long pecaId, int quantidade) {
        OrdemServico os = osRepository.findById(osId)
                .orElseThrow(() -> new RuntimeException("OS não encontrada: " + osId));

        if (os.getStatus() == StatusOS.CONCLUIDA || os.getStatus() == StatusOS.CANCELADA) {
            throw new RuntimeException("Não é possível adicionar peças a uma OS " + os.getStatus().getDescricao());
        }

        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new RuntimeException("Peça não encontrada: " + pecaId));

        if (peca.getQuantidade() < quantidade) {
            throw new RuntimeException("Estoque insuficiente para \"" + peca.getNome()
                    + "\". Disponível: " + peca.getQuantidade());
        }

        // Debita estoque
        peca.setQuantidade(peca.getQuantidade() - quantidade);
        pecaRepository.save(peca);

        // Cria item na OS
        ItemOS item = new ItemOS(os, peca, quantidade);
        itemOSRepository.save(item);

        // Atualiza status do veículo
        Veiculo veiculo = os.getVeiculo();
        if (veiculo.getStatus() != StatusVeiculo.EM_MANUTENCAO) {
            veiculo.setStatus(StatusVeiculo.EM_MANUTENCAO);
            veiculoRepository.save(veiculo);
        }
    }

    // ── REMOVER PEÇA DA OS ────────────────────────────────────────

    @Transactional
    public void removerItem(Long itemId) {
        ItemOS item = itemOSRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + itemId));

        // Devolve ao estoque
        Peca peca = item.getPeca();
        peca.setQuantidade(peca.getQuantidade() + item.getQuantidade());
        pecaRepository.save(peca);

        itemOSRepository.delete(item);
    }

    // ── MÃO DE OBRA ───────────────────────────────────────────────

    @Transactional
    public void salvarMaoObra(Long osId,
                              Boolean usarMaoObra,
                              Boolean porPercentual,
                              Double valorFixo,
                              Double percentual) {

        OrdemServico os = osRepository.findById(osId)
                .orElseThrow(() -> new RuntimeException("OS não encontrada: " + osId));

        if (Boolean.FALSE.equals(usarMaoObra)) {
            // Usuário optou por não cobrar mão de obra
            os.setValorMaoObra(null);
            os.setPercentualMaoObra(null);
            os.setMaoObraPorPercentual(false);
        } else if (Boolean.TRUE.equals(porPercentual)) {
            os.setMaoObraPorPercentual(true);
            os.setPercentualMaoObra(percentual);
            os.setValorMaoObra(null);
        } else {
            os.setMaoObraPorPercentual(false);
            os.setValorMaoObra(valorFixo);
            os.setPercentualMaoObra(null);
        }

        osRepository.save(os);
    }

    // ── ATUALIZAR STATUS DA OS ────────────────────────────────────

    @Transactional
    public void atualizarStatus(Long osId, StatusOS novoStatus) {
        OrdemServico os = osRepository.findById(osId)
                .orElseThrow(() -> new RuntimeException("OS não encontrada: " + osId));

        os.setStatus(novoStatus);

        // Sincroniza status do veículo
        Veiculo veiculo = os.getVeiculo();
        switch (novoStatus) {
            case CONCLUIDA -> {
                veiculo.setStatus(StatusVeiculo.MANUTENCAO_FINALIZADA);
                os.setDataConclusao(LocalDate.now());
            }
            case AGUARDANDO_PECA -> veiculo.setStatus(StatusVeiculo.AGUARDANDO_PECA);
            case EM_ANDAMENTO, ABERTA -> veiculo.setStatus(StatusVeiculo.EM_MANUTENCAO);
            case CANCELADA -> {
                veiculo.setStatus(StatusVeiculo.MANUTENCAO_FINALIZADA);
                os.setDataConclusao(LocalDate.now());
            }
        }

        veiculoRepository.save(veiculo);
        osRepository.save(os);
    }

    // ── SALVAR (atualizar observações/descrição) ──────────────────

    @Transactional
    public OrdemServico salvar(OrdemServico os) {
        return osRepository.save(os);
    }
}