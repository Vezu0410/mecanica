package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Funcionario;
import com.garageautobot.garagemautobot.entities.LoginForm;
import com.garageautobot.garagemautobot.repositories.FuncionarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    // Página de login
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    // Processa login
    @PostMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm form, Model model, HttpSession session) {
        Optional<Funcionario> funcionarioOpt =
                funcionarioRepository.findByCpfAndSenha(form.getCpf(), form.getSenha());

        if (funcionarioOpt.isPresent()) {
            // Guarda o usuário logado na sessão
            session.setAttribute("usuarioLogado", funcionarioOpt.get());
            return "redirect:/menu"; 
        } else {
            model.addAttribute("error", "CPF ou senha inválidos");
            return "login";
        }
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // limpa a sessão
        return "redirect:/login";
    }
}
