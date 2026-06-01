package com.garageautobot.garagemautobot.services;

import com.garageautobot.garagemautobot.entities.Peca;
import com.garageautobot.garagemautobot.entities.PecaUsada;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.repositories.PecaRepository;
import com.garageautobot.garagemautobot.repositories.PecaUsadaRepository;
import com.garageautobot.garagemautobot.repositories.VeiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PecaUsadaService {

    private final PecaUsadaRepository pecaUsadaRepository;
    private final PecaRepository pecaRepository;
    private final VeiculoRepository veiculoRepository;

    public PecaUsadaService(PecaUsadaRepository pecaUsadaRepository,
                            PecaRepository pecaRepository,
                            VeiculoRepository veiculoRepository) {
        this.pecaUsadaRepository = pecaUsadaRepository;
        this.pecaRepository = pecaRepository;
        this.veiculoRepository = veiculoRepository;
    }

    @Transactional
    public void registrarUsoDePeca(Long veiculoId, Long pecaId, int quantidadeUsada) {
        Veiculo veiculo = veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new RuntimeException("Peça não encontrada"));

        if (peca.getQuantidade() < quantidadeUsada) {
            throw new RuntimeException("Estoque insuficiente da peça: " + peca.getNome());
        }

        // Diminui do estoque
        peca.setQuantidade(peca.getQuantidade() - quantidadeUsada);
        pecaRepository.save(peca);

        // Cria o vínculo e salva
        PecaUsada usada = new PecaUsada(veiculo, peca, quantidadeUsada, peca.getPrecoUnitario());
        pecaUsadaRepository.save(usada);
    }
}
