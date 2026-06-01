package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Funcionario;
import com.garageautobot.garagemautobot.services.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping("/funcionarios/cadastrar")
    public String mostrarForm(Model model) {
        model.addAttribute("funcionario", new Funcionario());
        return "cadastro-funcionario";
    }

    @PostMapping("/funcionarios/salvar")
    public String salvarFuncionario(@ModelAttribute Funcionario funcionario, Model model) {
        try {
            funcionarioService.salvarFuncionario(funcionario);
            model.addAttribute("successMessage", "Funcionário cadastrado com sucesso!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao cadastrar funcionário: " + e.getMessage());
        }
        model.addAttribute("funcionario", new Funcionario());
        return "cadastro-funcionario";
    }
}
