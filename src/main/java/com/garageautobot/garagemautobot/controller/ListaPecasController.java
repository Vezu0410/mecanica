package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Peca;
import com.garageautobot.garagemautobot.services.PecaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;

@Controller
public class ListaPecasController {

    private final PecaService pecaService;

    @Autowired
    public ListaPecasController(PecaService pecaService) {
        this.pecaService = pecaService;
    }

    @GetMapping("/pecas/lista")
    public String listarPecas(Model model) {
        List<Peca> pecas = pecaService.findAll();

        // Formata data e preço para evitar erro no Thymeleaf
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<Map<String, Object>> pecasFormatadas = pecas.stream().map(peca -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", peca.getId());
            map.put("nome", peca.getNome());
            map.put("codigo", peca.getCodigo());
            map.put("quantidade", peca.getQuantidade());
            map.put("precoUnitarioFormatado", 
                    String.format(Locale.forLanguageTag("pt-BR"), "%.2f", peca.getPrecoUnitario()));
            map.put("dataCadastro", peca.getDataCadastro().format(formatter));
            return map;
        }).collect(Collectors.toList());

        model.addAttribute("pecas", pecasFormatadas);
        return "lista-pecas";
    }
    
    
    @GetMapping("/pecas/editar/{id}")
    public String editarPeca(@PathVariable Long id, Model model) {
        Peca peca = pecaService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada: " + id));

        model.addAttribute("peca", peca);
        return "cadastro-peca"; // reutiliza o mesmo formulário
    }

    
}
