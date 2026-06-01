package com.garageautobot.garagemautobot.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns(
                    "/menu/**",
                    "/veiculos/**",
                    "/pecas/**",
                    "/clientes/**",   // bug corrigido: espaço removido
                    "/relatorios/**",
                    "/funcionarios/**",
                    "/editar/**",
                    "/os/**"          // novo: protege rotas de Ordem de Serviço
                )
                .excludePathPatterns(
                    "/login",
                    "/login/**",
                    "/css/**",
                    "/js/**",
                    "/uploads/**"
                );
    }
}