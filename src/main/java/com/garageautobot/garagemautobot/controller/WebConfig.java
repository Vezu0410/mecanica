package com.garageautobot.garagemautobot.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/menu/**", "/veiculos/**", "/pecas/**", "/clientes/** ", "/relatorios/**", "/funcionarios/**", "/editar/**") // rotas que quer proteger
                .excludePathPatterns("/login", "/login/**", "/css/**", "/js/**"); // libera login e recursos estáticos
    }
}
