package com.garageautobot.garagemautobot.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 1) Interceptor de LOGIN - protege todas as areas internas do sistema
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns(
                    "/menu/**",
                    "/veiculos/**",
                    "/pecas/**",
                    "/clientes/**",
                    "/relatorios/**",
                    "/funcionarios/**",
                    "/editar/**",
                    "/os/**",
                    "/agenda/**",
                    "/fotos/**",
                    "/backup/**"
                )
                .excludePathPatterns(
                    "/login",
                    "/login/**",
                    "/css/**",
                    "/js/**",
                    "/uploads/**",
                    "/acesso-negado"
                );

        // 2) Interceptor de ADMIN - protege apenas as rotas exclusivas de administrador.
        //    Roda depois do login. Quem nao for ADMIN e mandado para /acesso-negado.
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns(
                    "/funcionarios/cadastrar",      // criar funcionario
                    "/funcionarios/salvar",         // salvar funcionario
                    "/funcionarios/editar/**",      // editar funcionario (caso exista futuramente)
                    "/funcionarios/excluir/**",     // excluir funcionario (caso exista futuramente)
                    "/funcionarios/inativar/**",    // inativar funcionario e so admin
                    "/funcionarios/reativar/**",    // reativar funcionario e so admin
                    "/backup/**",                   // backup e exclusivo do admin
                    "/pecas/reativar/**",           // reativar peca e exclusivo do admin
                    "/veiculos/reativar/**",        // reativar veiculo e exclusivo do admin
                    "/clientes/reativar/**"         // reativar cliente e exclusivo do admin
                );
    }
}