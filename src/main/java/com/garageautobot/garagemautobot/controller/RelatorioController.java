package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Peca;
import com.garageautobot.garagemautobot.entities.Veiculo;
import com.garageautobot.garagemautobot.services.PecaService;
import com.garageautobot.garagemautobot.services.VeiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@Controller
public class RelatorioController {

    private final PecaService pecaService;
    private final VeiculoService veiculoService;

    @Autowired
    public RelatorioController(PecaService pecaService, VeiculoService veiculoService) {
        this.pecaService = pecaService;
        this.veiculoService = veiculoService;
    }

    @GetMapping("/relatorios")
    public String relatorios(
            @RequestParam(name = "searchPeca", required = false) String searchPeca,
            @RequestParam(name = "searchVeiculo", required = false) String searchVeiculo,
            Model model) {

        DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Lista de peças filtradas
        List<Map<String, Object>> pecas = pecaService.findAll().stream()
                .filter(p -> searchPeca == null || searchPeca.isEmpty() ||
                        p.getNome().toLowerCase().contains(searchPeca.toLowerCase()))
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("nome", p.getNome());
                    map.put("codigo", p.getCodigo());
                    map.put("quantidade", p.getQuantidade());
                    map.put("precoUnitario", String.format(Locale.forLanguageTag("pt-BR"), "%.2f", p.getPrecoUnitario()));
                    map.put("valorTotal", String.format(Locale.forLanguageTag("pt-BR"), "%.2f", p.getValorTotalEstoque()));
                    map.put("dataCadastro", p.getDataCadastro().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                    return map;
                }).collect(Collectors.toList());
        List<Map<String, Object>> veiculos = veiculoService.findAll().stream()
                .filter(v -> searchVeiculo == null || searchVeiculo.isEmpty() ||
                        v.getMarca().toLowerCase().contains(searchVeiculo.toLowerCase()) ||
                        v.getModelo().toLowerCase().contains(searchVeiculo.toLowerCase()) ||
                        v.getPlaca().toLowerCase().contains(searchVeiculo.toLowerCase()))
                .map(v -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("marca", v.getMarca());
                    map.put("modelo", v.getModelo());
                    map.put("ano", v.getAno());
                    map.put("placa", v.getPlaca());
                    map.put("status", v.getStatus());
                    map.put("cliente", v.getCliente().getNome());
                    map.put("dataCadastro", v.getDataCadastro().format(dataFormatter));
                    return map;
                }).collect(Collectors.toList());

        model.addAttribute("pecas", pecas);
        model.addAttribute("veiculos", veiculos);
        model.addAttribute("searchPeca", searchPeca);
        model.addAttribute("searchVeiculo", searchVeiculo);
        return "relatorios";
    }
}
