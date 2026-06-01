package com.garageautobot.garagemautobot.entities;

public enum MomentoFoto {
    ENTRADA,
    SAIDA;

    public String getDescricao() {
        return switch (this) {
            case ENTRADA -> "Entrada do Veículo";
            case SAIDA   -> "Saída / Entrega";
        };
    }
}