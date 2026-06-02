package com.garageautobot.garagemautobot.entities;

public enum StatusAgendamento {
    AGENDADO,      // marcado, aguardando o carro chegar
    COMPARECEU,    // carro chegou e virou OS
    NAO_COMPARECEU,// cliente não apareceu (faltou)
    CANCELADO;     // agendamento cancelado

    public String getDescricao() {
        return switch (this) {
            case AGENDADO       -> "Agendado";
            case COMPARECEU     -> "Compareceu";
            case NAO_COMPARECEU -> "Não Compareceu";
            case CANCELADO      -> "Cancelado";
        };
    }
}