package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.StatusVeiculo;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.repositories.VeiculoRepository;
import com.garageautobot.garagemautobot.services.OrdemServicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AtualzarStatusController {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private OrdemServicoService ordemServicoService;

    @PostMapping("/veiculos/{id}/status")
    public String atualizarStatus(@PathVariable Long id,
                                  @RequestParam("novoStatus") StatusVeiculo novoStatus) {

        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        // 1. Atualiza o status do veículo
        veiculo.setStatus(novoStatus);
        veiculoRepository.save(veiculo);

        // 2. Sincroniza a OS ativa do veículo (se houver), mantendo
        //    veículo e ordem de serviço sempre coerentes.
        ordemServicoService.sincronizarComStatusVeiculo(id, novoStatus);

        return "redirect:/menu";
    }
}