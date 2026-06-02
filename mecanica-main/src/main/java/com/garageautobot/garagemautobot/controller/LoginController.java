package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Funcionario;
import com.garageautobot.garagemautobot.entities.LoginForm;
import com.garageautobot.garagemautobot.services.FuncionarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private FuncionarioService funcionarioService;

    // Página de login
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    // Processa login — agora usa autenticação com BCrypt
    @PostMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm form,
                        Model model, HttpSession session) {

        Optional<Funcionario> funcionarioOpt =
                funcionarioService.autenticar(form.getCpf(), form.getSenha());

        if (funcionarioOpt.isPresent()) {
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
        session.invalidate();
        return "redirect:/login";
    }
}