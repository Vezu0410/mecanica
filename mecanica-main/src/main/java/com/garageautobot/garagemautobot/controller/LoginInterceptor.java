package com.garageautobot.garagemautobot.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Verifica se o usuário está logado na sessão
        Object usuarioLogado = request.getSession().getAttribute("usuarioLogado");
        
        if (usuarioLogado == null) {
            // Usuário não está logado → redireciona para /login
            response.sendRedirect("/login");
            return false; // não chama o controller
        }
        
        return true; // usuário logado → segue pro controller
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {}

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {}
}
