package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.*;
import com.garageautobot.garagemautobot.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/veiculos")
public class VeiculoPecaController {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private PecaRepository pecaRepository;

    @Autowired
    private VeiculoPecaRepository veiculoPecaRepository;

    @PostMapping("/{id}/adicionar-peca")
    public String adicionarPeca(
            @PathVariable Long id,
            @RequestParam Long pecaId,
            @RequestParam int quantidadeUsada) {

        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
        Peca peca = pecaRepository.findById(pecaId)
                .orElseThrow(() -> new RuntimeException("Peça não encontrada"));

        if (peca.getQuantidade() < quantidadeUsada) {
            throw new RuntimeException("Estoque insuficiente da peça: " + peca.getNome());
        }

        // Criar relação
        VeiculoPeca relacao = new VeiculoPeca(veiculo, peca, quantidadeUsada);
        veiculoPecaRepository.save(relacao);

        // Atualizar estoque
        peca.setQuantidade(peca.getQuantidade() - quantidadeUsada);
        pecaRepository.save(peca);

        return "redirect:/menu";
    }
}
