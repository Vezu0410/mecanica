package com.garageautobot.garagemautobot.controller;


import com.garageautobot.garagemautobot.entities.Funcionario;
import com.garageautobot.garagemautobot.entities.LoginForm;
import com.garageautobot.garagemautobot.repositories.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginForm") LoginForm form, Model model) {
        Optional<Funcionario> funcionarioOpt =
                funcionarioRepository.findByCpfAndSenha(form.getCpf(), form.getSenha());

        if (funcionarioOpt.isPresent()) {
            model.addAttribute("message", "Login realizado com sucesso!");
            return "home"; 
        } else {
            model.addAttribute("error", "CPF ou senha inválidos");
            return "login";
        }
    }
}
