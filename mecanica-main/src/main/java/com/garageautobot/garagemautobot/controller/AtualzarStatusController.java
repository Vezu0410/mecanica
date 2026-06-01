package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.StatusVeiculo;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.repositories.VeiculoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AtualzarStatusController {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @PostMapping("/veiculos/{id}/status")
    public String atualizarStatus(@PathVariable Long id, @RequestParam("novoStatus") StatusVeiculo novoStatus) {
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        veiculo.setStatus(novoStatus);
        veiculoRepository.save(veiculo);

        return "redirect:/menu";
    }
}