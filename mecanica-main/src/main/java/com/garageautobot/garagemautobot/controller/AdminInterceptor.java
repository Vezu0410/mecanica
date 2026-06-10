package com.garageautobot.garagemautobot.controller;

import com.garageautobot.garagemautobot.entities.Funcionario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor que protege rotas exclusivas de administrador.
 * Deve ser registrado DEPOIS do LoginInterceptor (que garante que há alguém logado).
 *
 * Se o usuário logado não for ADMIN, redireciona para a página de acesso negado.
 */
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        Object usuarioLogado = request.getSession().getAttribute("usuarioLogado");

        // Se não houver ninguém logado, deixa o fluxo de login tratar
        if (usuarioLogado == null) {
            response.sendRedirect("/login");
            return false;
        }

        // Verifica o papel
        if (usuarioLogado instanceof Funcionario funcionario) {
            if (funcionario.isAdmin()) {
                return true; // é admin -> libera
            }
        }

        // Nao e admin -> bloqueia mostrando a pagina de acesso negado
        response.sendRedirect("/acesso-negado");
        return false;
    }
}