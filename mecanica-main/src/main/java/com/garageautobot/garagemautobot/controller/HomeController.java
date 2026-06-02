package com.garageautobot.garagemautobot.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Redireciona a raiz do sistema ("/") para o local correto:
     * - Se o usuário já está logado  → vai para o menu
     * - Se não está logado           → vai para o login
     * caso ele acesse por / e n por / login
     * Isso resolve o acesso pela raiz tanto no navegador quanto no app Android.
     */
	
    @GetMapping("/")
    public String home(HttpSession session) {
        Object usuarioLogado = session.getAttribute("usuarioLogado");
        if (usuarioLogado != null) {
            return "redirect:/menu";
        }
        return "redirect:/login";
    }
}