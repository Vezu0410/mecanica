package com.garageautobot.garagemautobot.entities;

public enum StatusOS {
    ABERTA,
    EM_ANDAMENTO,
    AGUARDANDO_PECA,
    CONCLUIDA,
    CANCELADA;

    public String getDescricao() {
        return switch (this) {
            case ABERTA          -> "Aberta";
            case EM_ANDAMENTO    -> "Em Andamento";
            case AGUARDANDO_PECA -> "Aguardando Peça";
            case CONCLUIDA       -> "Concluída";
            case CANCELADA       -> "Cancelada";
        };
    }
}