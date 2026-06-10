package com.garageautobot.garagemautobot.entities;

public enum PapelFuncionario {
    ADMIN,        //  — acesso total
    FUNCIONARIO;  //  — acesso operacional

    public String getDescricao() {
        return switch (this) {
            case ADMIN       -> "Administrador";
            case FUNCIONARIO -> "Funcionário";
        };
    }
}