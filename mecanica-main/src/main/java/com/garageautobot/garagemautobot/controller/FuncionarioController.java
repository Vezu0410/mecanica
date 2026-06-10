package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Funcionario;
import com.garageautobot.garagemautobot.services.FuncionarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            model.addAttribute("funcionario", new Funcionario());
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("funcionario", funcionario);
        }
        return "cadastro-funcionario";
    }

    // Listagem de funcionarios (funcionario comum pode VER; gerenciamento e so admin)
    @GetMapping("/funcionarios/listar")
    public String listar(Model model) {
        model.addAttribute("funcionarios", funcionarioService.listarAtivos());
        model.addAttribute("funcionariosInativos", funcionarioService.listarInativos());
        return "lista-funcionarios";
    }

    // INATIVAR funcionario (soft delete) - so admin (protegido no WebConfig)
    @PostMapping("/funcionarios/inativar/{id}")
    public String inativar(@PathVariable Long id, HttpSession session, RedirectAttributes ra) {
        try {
            // Descobre o id do usuario logado para a trava "nao inativar a si mesmo"
            Long idLogado = null;
            Object logado = session.getAttribute("usuarioLogado");
            if (logado instanceof Funcionario f) {
                idLogado = f.getId();
            }

            funcionarioService.inativar(id, idLogado);
            ra.addFlashAttribute("successMessage", "Funcionário inativado. Ele não pode mais acessar o sistema, mas o histórico foi preservado.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/funcionarios/listar";
    }

    // REATIVAR funcionario - so admin (protegido no WebConfig)
    @PostMapping("/funcionarios/reativar/{id}")
    public String reativar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            funcionarioService.reativar(id);
            ra.addFlashAttribute("successMessage", "Funcionário reativado com sucesso!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/funcionarios/listar";
    }
}