package com.garageautobot.garagemautobot.services;

import com.garageautobot.garagemautobot.entities.StatusVeiculo;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.repositories.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.garageautobot.garagemautobot.entities.StatusVeiculo;


import java.util.List;
import java.util.Optional;

@Service
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;

    @Autowired
    public VeiculoService(VeiculoRepository veiculoRepository) {
        this.veiculoRepository = veiculoRepository;
    }

    public List<Veiculo> findAll() {
        return veiculoRepository.findAll();
    }

    public Optional<Veiculo> findById(Long id) {
        return veiculoRepository.findById(id);
    }

    public List<Veiculo> findByCliente(Long clienteId) {
        return veiculoRepository.findByClienteId(clienteId);
    }

    public Veiculo save(Veiculo veiculo) {
        return veiculoRepository.save(veiculo);
    }

    public void delete(Long id) {
        veiculoRepository.deleteById(id);
    }
    
    public List<Veiculo> findByStatusEmManutencaoOuAguardando() {
        return veiculoRepository.findByStatusIn(
            List.of(StatusVeiculo.EM_MANUTENCAO, StatusVeiculo.AGUARDANDO_PECA)
        );
    }
    
}
