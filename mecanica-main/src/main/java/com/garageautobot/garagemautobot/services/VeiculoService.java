package com.garageautobot.garagemautobot.services;

import com.garageautobot.garagemautobot.entities.StatusVeiculo;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.repositories.OrdemServicoRepository;
import com.garageautobot.garagemautobot.repositories.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final OrdemServicoRepository ordemServicoRepository;

    @Autowired
    public VeiculoService(VeiculoRepository veiculoRepository,
                          OrdemServicoRepository ordemServicoRepository) {
        this.veiculoRepository = veiculoRepository;
        this.ordemServicoRepository = ordemServicoRepository;
    }

    // findAll agora retorna SÓ os veículos ativos
    public List<Veiculo> findAll() {
        return veiculoRepository.findByAtivoTrue();
    }

    // Lista os inativos (para o admin reativar)
    public List<Veiculo> findInativos() {
        return veiculoRepository.findByAtivoFalse();
    }

    public Optional<Veiculo> findById(Long id) {
        return veiculoRepository.findById(id);
    }

    public List<Veiculo> findByCliente(Long clienteId) {
        return veiculoRepository.findByClienteIdAndAtivoTrue(clienteId);
    }

    public Veiculo save(Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    // INATIVAÇÃO (soft delete) com trava: não inativa veículo com OS em aberto
    public void inativar(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));

        // Trava: veículo com OS ativa (não concluída/cancelada) não pode ser inativado
        long osAbertas = ordemServicoRepository.findOSAtivasDoVeiculo(id).size();
        if (osAbertas > 0) {
            throw new IllegalStateException(
                "Não é possível inativar este veículo: há " + osAbertas +
                " ordem(ns) de serviço em aberto. Conclua ou cancele a(s) OS antes.");
        }

        veiculo.setAtivo(false);
        veiculoRepository.save(veiculo);
    }

    public void reativar(Long id) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));
        veiculo.setAtivo(true);
        veiculoRepository.save(veiculo);
    }

    public List<Veiculo> findByStatusEmManutencaoOuAguardando() {
        return veiculoRepository.findByStatusInAndAtivoTrue(
            List.of(StatusVeiculo.EM_MANUTENCAO, StatusVeiculo.AGUARDANDO_PECA)
        );
    }

    public List<Veiculo> findByStatus(StatusVeiculo status) {
        return veiculoRepository.findByStatusAndAtivoTrue(status);
    }

    public List<Veiculo> buscarPorStatus(String status) {
        if (status == null || status.equals("TODOS")) {
            return veiculoRepository.findByAtivoTrue();
        }
        try {
            StatusVeiculo statusEnum = StatusVeiculo.valueOf(status);
            return veiculoRepository.findByStatusAndAtivoTrue(statusEnum);
        } catch (IllegalArgumentException e) {
            return veiculoRepository.findByAtivoTrue();
        }
    }

    public List<Veiculo> search(String termo) {
        // Filtra a busca para mostrar apenas veículos ativos
        return veiculoRepository
            .findByMarcaContainingIgnoreCaseAndAtivoTrueOrModeloContainingIgnoreCaseAndAtivoTrueOrClienteNomeContainingIgnoreCaseAndAtivoTrue(
                termo, termo, termo
        );
    }
}